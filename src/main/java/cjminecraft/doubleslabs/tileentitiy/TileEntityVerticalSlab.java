package cjminecraft.doubleslabs.tileentitiy;

import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.util.WorldWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityVerticalSlab extends TileEntity implements ITickable {

    protected IBlockState negativeState;
    protected IBlockState positiveState;

    protected TileEntity negativeTile;
    protected TileEntity positiveTile;

    private WorldWrapper negativeWorld;
    private WorldWrapper positiveWorld;

    public TileEntityVerticalSlab() {

    }

    public WorldWrapper getNegativeWorld() {
        return this.negativeWorld;
    }

    public WorldWrapper getPositiveWorld() {
        return this.positiveWorld;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.negativeWorld = new WorldWrapper(world);
        this.negativeWorld.setVerticalSlab(this, false);
        this.positiveWorld = new WorldWrapper(world);
        this.positiveWorld.setVerticalSlab(this, true);
        if (this.negativeTile != null)
            this.negativeTile.setWorld(this.negativeWorld);
        if (this.positiveTile != null)
            this.positiveTile.setWorld(this.positiveWorld);
    }

    @Override
    public void setPos(BlockPos pos) {
        super.setPos(pos);
        if (this.negativeTile != null)
            this.negativeTile.setPos(this.pos);
        if (this.positiveTile != null)
            this.positiveTile.setPos(this.pos);
    }

    public IBlockState getNegativeState() {
        return this.negativeState;
    }

    public IBlockState getPositiveState() {
        return this.positiveState;
    }

    public void setNegativeState(IBlockState negativeState) {
//        this.negativeWorld.setBlockState(this.pos, negativeState);
//        this.negativeWorld.setTileEntity(this.pos, negativeState.createTileEntity(this.negativeWorld));
        if (this.negativeState != null && this.negativeState.getBlock().hasTileEntity() && this.negativeTile != null)
            this.negativeTile.updateContainingBlockInfo();
        if (this.negativeState != null && negativeState != null && (this.negativeState.getBlock() != negativeState.getBlock() || !negativeState.getBlock().hasTileEntity()) && this.negativeState.getBlock().hasTileEntity())
            setNegativeTile(null);
        if (this.negativeState == null)
            setNegativeTile(null);
        this.negativeState = negativeState;
        if (negativeState != null && negativeState.getBlock().hasTileEntity())
            if (this.negativeTile == null)
                setNegativeTile(negativeState.getBlock().createTileEntity(this.negativeWorld, this.negativeState));
            else
                this.negativeTile.updateContainingBlockInfo();
        markDirtyClient();
    }

    public void setPositiveState(IBlockState positiveState) {
//        this.positiveWorld.setBlockState(this.pos, positiveState);
//        this.positiveWorld.setTileEntity(this.pos, positiveState.createTileEntity(this.positiveWorld));
        if (this.positiveState != null && this.positiveState.getBlock().hasTileEntity() && this.positiveTile != null)
            this.positiveTile.updateContainingBlockInfo();
        if (this.positiveState != null && positiveState != null && (this.positiveState.getBlock() != positiveState.getBlock() || !positiveState.getBlock().hasTileEntity()) && this.positiveState.getBlock().hasTileEntity())
            setPositiveTile(null);
        if (this.positiveState == null)
            setPositiveTile(null);
        this.positiveState = positiveState;
        if (positiveState != null && positiveState.getBlock().hasTileEntity())
            if (this.positiveTile == null)
                setPositiveTile(positiveState.getBlock().createTileEntity(this.positiveWorld, this.positiveState));
            else
                this.positiveTile.updateContainingBlockInfo();
        markDirtyClient();
    }

    public TileEntity getNegativeTile() {
        return this.negativeTile;
    }

    public void setNegativeTile(TileEntity negativeTile) {
        prepareTile(negativeTile, false);
        this.negativeTile = negativeTile;
    }

    public TileEntity getPositiveTile() {
        return this.positiveTile;
    }

    public void setPositiveTile(TileEntity positiveTile) {
        prepareTile(positiveTile, true);
        this.positiveTile = positiveTile;
    }

    private void prepareTile(TileEntity tile, boolean positive) {
        if (tile != null) {
            tile.setWorld(this.world);
            tile.setPos(this.pos);
            if (positive)
                if (this.positiveTile != null)
                    this.positiveTile.invalidate();
            else
                if (this.negativeTile != null)
                    this.negativeTile.invalidate();
            tile.onLoad();
            tile.setWorld(positive ? this.positiveWorld : this.negativeWorld);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (this.negativeState != null)
            nbt.setTag("negative", NBTUtil.writeBlockState(new NBTTagCompound(), this.negativeState));
        if (this.negativeTile != null)
            nbt.setTag("negative_tile", this.negativeTile.writeToNBT(new NBTTagCompound()));
        if (this.positiveState != null)
            nbt.setTag("positive", NBTUtil.writeBlockState(new NBTTagCompound(), this.positiveState));
        if (this.positiveTile != null)
            nbt.setTag("positive_tile", this.positiveTile.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound read) {
        super.readFromNBT(read);
        if (read.hasKey("negative"))
            this.negativeState = NBTUtil.readBlockState(read.getCompoundTag("negative"));
        if (read.hasKey("negative_tile"))
            this.negativeTile = TileEntity.create(this.world, read.getCompoundTag("negative_tile"));
        if (read.hasKey("positive"))
            this.positiveState = NBTUtil.readBlockState(read.getCompoundTag("positive"));
        if (read.hasKey("positive_tile"))
            this.positiveTile = TileEntity.create(this.world, read.getCompoundTag("positive_tile"));
        markDirtyClient();
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readFromNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        int metadata = getBlockMetadata();
        return new SPacketUpdateTileEntity(this.pos, metadata, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    @Nonnull
    public NBTTagCompound getTileData() {
        return this.writeToNBT(new NBTTagCompound());
    }

    private void markDirtyClient() {
        markDirty();
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(getPos());
            this.world.checkLight(this.pos);
            this.world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public void update() {
        if (this.world != null && this.positiveWorld != null && this.negativeWorld != null) {
            if (this.positiveTile != null && this.positiveTile instanceof ITickable) {
                if (this.positiveTile.getWorld() == null) {
                    this.positiveTile.setWorld(this.positiveWorld);
                    this.positiveTile.setPos(this.pos);
                }
                ((ITickable) this.positiveTile).update();
            }
            if (this.negativeTile != null && this.negativeTile instanceof ITickable) {
                if (this.negativeTile.getWorld() == null) {
                    this.negativeTile.setWorld(this.negativeWorld);
                    this.negativeTile.setPos(this.pos);
                }
                ((ITickable) this.negativeTile).update();
            }
        }
    }

    @Override
    public void onLoad() {
        if (this.positiveTile != null) {
            this.positiveTile.setWorld(this.positiveWorld);
            this.positiveTile.setPos(this.pos);
            this.positiveTile.onLoad();
        }
        if (this.negativeTile != null) {
            this.negativeTile.setWorld(this.negativeWorld);
            this.negativeTile.setPos(this.pos);
            this.negativeTile.onLoad();
        }
    }


    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (this.positiveTile != null)
            this.positiveTile.onChunkUnload();
        if (this.negativeTile != null)
            this.negativeTile.onChunkUnload();
    }

    @Override
    public void rotate(Rotation rotation) {
        super.rotate(rotation);
        if (this.positiveTile != null)
            this.positiveTile.rotate(rotation);
        if (this.negativeTile != null)
            this.negativeTile.rotate(rotation);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.positiveTile != null) {
            this.positiveTile.setWorld(this.world);
            this.positiveTile.invalidate();
        }
        if (this.negativeTile != null) {
            this.negativeTile.setWorld(this.world);
            this.negativeTile.invalidate();
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (this.positiveTile != null)
            this.positiveTile.validate();
        if (this.negativeTile != null)
            this.negativeTile.validate();
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.positiveTile != null)
            this.positiveTile.updateContainingBlockInfo();
        if (this.negativeTile != null)
            this.negativeTile.updateContainingBlockInfo();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (this.positiveTile != null && this.positiveTile.hasCapability(capability, facing)) || (this.negativeTile != null && this.negativeTile.hasCapability(capability, facing));
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (this.positiveTile != null && this.negativeTile == null)
            return this.positiveTile.getCapability(capability, facing);
        else if (this.negativeTile != null && this.positiveTile == null)
            return this.negativeTile.getCapability(capability, facing);
        else if (this.positiveTile != null && this.negativeTile != null) {
            if (this.positiveTile.hasCapability(capability, facing))
                return this.positiveTile.getCapability(capability, facing);
            if (this.negativeTile.hasCapability(capability, facing))
                return this.negativeTile.getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
