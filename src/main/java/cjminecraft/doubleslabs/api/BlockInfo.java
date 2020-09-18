package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class BlockInfo implements IBlockInfo, INBTSerializable<NBTTagCompound>, ICapabilityProvider {

    private ISlabSupport support;
    private IBlockState state;
    private TileEntity tile;
    private IWorldWrapper<?> world;

    private final SlabTileEntity slab;
    private final boolean positive;

    public BlockInfo(SlabTileEntity slab, boolean positive) {
        this.slab = slab;
        this.positive = positive;
    }

    @Nullable
    @Override
    public IBlockState getBlockState() {
        return this.state;
    }

    @Nullable
    @Override
    public IBlockState getExtendedBlockState() {
        return this.state != null ? this.state.getBlock().getExtendedState(this.state, this.getWorld(), this.getPos()) : null;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity() {
        return this.tile;
    }

    @Nonnull
    @Override
    public World getWorld() {
        return (World) this.world;
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
    public void setBlockState(@Nullable IBlockState state) {
        if (this.state != null && this.state.getBlock().hasTileEntity(this.state) && this.tile != null)
            this.tile.updateContainingBlockInfo();
        if (this.state != null && state != null && (this.state.getBlock() != state.getBlock() || !state.getBlock().hasTileEntity(state)) && this.state.getBlock().hasTileEntity(this.state))
            setTileEntity(null);
        if (this.state == null)
            setTileEntity(null);
        this.state = state;
        if (state != null && state.getBlock().hasTileEntity(state))
            if (this.tile == null)
                setTileEntity(state.getBlock().createTileEntity(this.getWorld(), state));
            else
                this.tile.updateContainingBlockInfo();
        this.slab.markDirtyClient();
        this.support = state == null ? null : SlabSupport.getSlabSupport(this.getWorld(), this.getPos(), this.state);
    }

    @Override
    public void setTileEntity(@Nullable TileEntity tile) {
        if (tile != null) {
            if (this.tile != null) {
                this.tile.setWorld(this.slab.getWorld());
                this.tile.setPos(this.slab.getPos());
                this.tile.invalidate();
            }
            tile.setWorld(this.slab.getWorld());
            tile.setPos(this.slab.getPos());
            tile.onLoad();
            tile.setWorld(getWorld());
        }
        this.tile = tile;
    }

    public void setWorld(World world) {
        if (this.world != null)
            this.world.setWorld(world);
        else
            this.world = world instanceof WorldServer ? new ServerWorldWrapper((WorldServer) world) : new WorldWrapper(world);
        this.world.setPositive(this.positive);
        this.world.setBlockPos(this.slab.getPos());
        this.world.setStateContainer(this.slab);

        if (this.tile != null)
            this.tile.setWorld(this.getWorld());
    }

    public void setPos(BlockPos pos) {
        this.world.setBlockPos(pos);
        if (this.tile != null)
            this.tile.setPos(pos);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        if (this.state != null)
            nbt.setTag("state", NBTUtil.writeBlockState(new NBTTagCompound(), this.state));
        if (this.tile != null)
            nbt.setTag("tile", this.tile.writeToNBT(new NBTTagCompound()));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("state"))
            this.state = NBTUtil.readBlockState(nbt.getCompoundTag("state"));
        if (nbt.hasKey("tile"))
            this.tile = TileEntity.create(getWorld(), nbt.getCompoundTag("tile"));
    }

    public void onLoad() {
        if (this.tile != null) {
            this.tile.setWorld(this.getWorld());
            this.tile.setPos(this.getPos());
            this.tile.onLoad();
        }
    }

    public void onChunkUnloaded() {
        if (this.tile != null)
            this.tile.onChunkUnload();
    }

    public void remove() {
        if (this.tile != null) {
            this.tile.setWorld(this.slab.getWorld());
            this.tile.setPos(this.slab.getPos());
            this.tile.invalidate();
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

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return this.tile != null && this.tile.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return this.tile != null ? this.tile.getCapability(capability, facing) : null;
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
