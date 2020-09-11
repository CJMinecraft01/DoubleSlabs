package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class BlockInfo implements IBlockInfo, INBTSerializable<CompoundNBT>, ICapabilityProvider {

    private ISlabSupport support;
    private BlockState state;
    private TileEntity tile;
    private WorldWrapper world;

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
    public TileEntity getTileEntity() {
        return this.tile;
    }

    @Nonnull
    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean isPositive() {
        return this.positive;
    }

    @Override
    public BlockPos getPos() {
        return this.slab.getPos();
    }

    @Nullable
    @Override
    public ISlabSupport getSupport() {
        return this.support;
    }

    @Override
    public void setBlockState(@Nullable BlockState state) {
        if (this.state != null && this.state.hasTileEntity() && this.tile != null)
            this.tile.updateContainingBlockInfo();
        if (this.state != null && state != null && (this.state.getBlock() != state.getBlock() || !state.hasTileEntity()) && this.state.hasTileEntity())
            setTileEntity(null);
        if (this.state == null)
            setTileEntity(null);
        this.state = state;
        if (state != null && state.hasTileEntity())
            if (this.tile == null)
                setTileEntity(state.createTileEntity(this.world));
            else
                this.tile.updateContainingBlockInfo();
        this.slab.markDirtyClient();
        this.support = state == null ? null : SlabSupport.getSlabSupport(this.world, this.getPos(), this.state);
    }

    @Override
    public void setTileEntity(@Nullable TileEntity tile) {
        if (tile != null) {
            tile.setWorldAndPos(this.slab.getWorld(), this.slab.getPos());
            if (this.tile != null) {
                this.tile.setWorldAndPos(this.slab.getWorld(), this.slab.getPos());
                this.tile.remove();
            }
            tile.onLoad();
            tile.setWorldAndPos(this.world, this.slab.getPos());
        }
        this.tile = tile;
    }

    public void setWorld(World world) {
        if (this.world != null)
            this.world.setWorld(world);
        else
            this.world = new WorldWrapper(world);
        this.world.setTile(this.slab, this.positive);

        if (this.tile != null)
            this.tile.setWorldAndPos(this.world, this.slab.getPos());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        if (this.state != null)
            nbt.put("state", NBTUtil.writeBlockState(this.state));
        if (this.tile != null)
            nbt.put("tile", this.tile.write(new CompoundNBT()));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("state"))
            this.state = NBTUtil.readBlockState(nbt.getCompound("state"));
        if (nbt.contains("tile"))
            this.tile = TileEntity.readTileEntity(this.state, nbt.getCompound("tile"));
    }

    public void onLoad() {
        if (this.tile != null) {
            this.tile.setWorldAndPos(this.world, this.slab.getPos());
            this.tile.onLoad();
        }
    }

    public void onChunkUnloaded() {
        if (this.tile != null)
            this.tile.onChunkUnloaded();
    }

    public void remove() {
        if (this.tile != null) {
            this.tile.setWorldAndPos(this.slab.getWorld(), this.slab.getPos());
            this.tile.remove();
            this.tile = null;
        }
    }

    public void validate() {
        if (this.tile != null)
            this.tile.validate();
    }

    public void updateContainingBlockInfo() {
        if (this.tile != null)
            this.tile.updateContainingBlockInfo();
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
}
