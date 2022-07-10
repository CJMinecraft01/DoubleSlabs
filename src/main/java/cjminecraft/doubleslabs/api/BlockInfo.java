package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class BlockInfo implements IBlockInfo, INBTSerializable<CompoundTag>, ICapabilityProvider {

    private ISlabSupport support;
    private BlockState state;
    private BlockEntity tile;
    private Level world;

    private final SlabTileEntity slab;
    private final boolean positive;

    public BlockInfo(SlabTileEntity slab, boolean positive) {
        this.slab = slab;
        this.positive = positive;
    }

    @Nullable
    @Override
    public BlockState getBlockState() {
        return this.state;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity() {
        return this.tile;
    }

    @Nonnull
    @Override
    public Level getWorld() {
        return this.world;
    }

    @Override
    public boolean isPositive() {
        return this.positive;
    }

    @Override
    public BlockPos getPos() {
        return this.slab.getBlockPos();
    }

    @Nullable
    @Override
    public ISlabSupport getSupport() {
        return this.support;
    }

    @Override
    public void setBlockState(@Nullable BlockState state) {
        if (this.state != null && this.state.hasBlockEntity() && this.tile != null)
            this.tile.setBlockState(this.state);
        if (this.state != null && state != null && (this.state.getBlock() != state.getBlock() || !state.hasBlockEntity()) && this.state.hasBlockEntity())
            setBlockEntity(null);
        if (this.state == null)
            setBlockEntity(null);
        this.state = state;
        this.support = state == null ? null : SlabSupport.getSlabSupport(this.getWorld(), this.getPos(), this.state);
        if (state != null && this.support != null && this.support.requiresWrappedWorld(this.state)) {
            // If the world is not wrapped, then we should wrap the world
            if (!(this.world instanceof IWorldWrapper<?>))
                wrapWorld(this.world);
            if (state.hasBlockEntity() && this.tile == null)
                setBlockEntity(((EntityBlock)state.getBlock()).newBlockEntity(this.slab.getBlockPos(), this.state));
            else if (this.tile != null) {
                this.tile.setBlockState(this.state);
            }
        }
        this.slab.markDirtyClient();
    }

    @Override
    public void setBlockEntity(@Nullable BlockEntity tile) {
        if (tile != null) {
            tile.setLevel(this.slab.getLevel());
            if (this.tile != null) {
                this.tile.setLevel(this.slab.getLevel());
                this.tile.setRemoved();
            }
            tile.onLoad();
            // If the world is not wrapped, then we should wrap the world
            if (!(this.world instanceof IWorldWrapper<?>))
                wrapWorld(this.world);
            tile.setLevel(this.getWorld());
        }
        this.tile = tile;
    }

    private void wrapWorld(Level world) {
        IWorldWrapper<?> w = world instanceof ServerLevel ? new ServerWorldWrapper((ServerLevel) world) : new WorldWrapper(world);
        w.setPositive(this.positive);
        w.setBlockPos(this.slab.getBlockPos());
        w.setStateContainer(this.slab);
        this.world = (Level) w;
    }

    public void setWorld(Level world) {
        if (this.world != null && this.world instanceof IWorldWrapper<?>)
            ((IWorldWrapper<?>) this.world).setWorld(world);
        else if (this.tile != null || (this.support != null && this.state != null && this.support.requiresWrappedWorld(this.state)))
            this.world = world instanceof ServerLevel ? new ServerWorldWrapper((ServerLevel) world) : new WorldWrapper(world);
        else
            this.world = world;

        if (this.world instanceof IWorldWrapper<?>) {
            IWorldWrapper<?> w = (IWorldWrapper<?>) this.world;
            w.setPositive(this.positive);
            w.setBlockPos(this.slab.getBlockPos());
            w.setStateContainer(this.slab);
        }

        if (this.tile != null)
            this.tile.setLevel(this.getWorld());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (this.state != null)
            nbt.put("state", NbtUtils.writeBlockState(this.state));
        if (this.tile != null)
            nbt.put("tile", this.tile.saveWithoutMetadata());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("state"))
            this.state = NbtUtils.readBlockState(nbt.getCompound("state"));
        if (nbt.contains("tile"))
            this.tile = BlockEntity.loadStatic(this.slab.getBlockPos(), this.state, nbt.getCompound("tile"));
        // If the world is not wrapped, then we should wrap the world
        if ((this.tile != null || (this.support != null && this.state != null && this.support.requiresWrappedWorld(this.state))) && this.world != null && !(this.world instanceof IWorldWrapper<?>))
            this.wrapWorld(this.world);
    }

    public void onLoad() {
        if (this.tile != null) {
            // If the world is not wrapped, then we should wrap the world
//            if (!(this.world instanceof IWorldWrapper<?>))
//                this.wrapWorld(this.world);
            this.tile.setLevel(this.getWorld());
            this.tile.onLoad();
        }
    }

    public void onChunkUnloaded() {
        if (this.tile != null)
            this.tile.onChunkUnloaded();
    }

    public void remove() {
        if (this.tile != null) {
            this.tile.setLevel(this.slab.getLevel());
            this.tile.setRemoved();
            this.tile = null;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return this.tile != null ? this.tile.getCapability(cap, side) : LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return this.tile != null ? this.tile.getCapability(cap) : LazyOptional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockInfo blockInfo = (BlockInfo) o;
        return positive == blockInfo.positive &&
                Objects.equals(state, blockInfo.state) &&
                Objects.equals(tile, blockInfo.tile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, tile, positive);
    }

    public boolean triggerEvent(int pA, int pB) {
        return this.tile != null && this.tile.triggerEvent(pA, pB);
    }
}
