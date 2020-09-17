package cjminecraft.doubleslabs.common.tileentity;

import cjminecraft.doubleslabs.api.BlockInfo;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class SlabTileEntity extends TileEntity implements IStateContainer, ITickable {

    protected final BlockInfo negativeBlockInfo = new BlockInfo(this, false);
    protected final BlockInfo positiveBlockInfo = new BlockInfo(this, true);

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.negativeBlockInfo.deserializeNBT(nbt.getCompoundTag("negativeBlock"));
        this.positiveBlockInfo.deserializeNBT(nbt.getCompoundTag("positiveBlock"));
        super.readFromNBT(nbt);
        markDirtyClient();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("negativeBlock", this.negativeBlockInfo.serializeNBT());
        nbt.setTag("positiveBlock", this.positiveBlockInfo.serializeNBT());
        return super.writeToNBT(nbt);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        this.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readFromNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getTileData() {
        return this.writeToNBT(new NBTTagCompound());
    }

    protected void markDirtyFast() {
        if (this.world != null)
            this.world.markChunkDirty(this.pos, this);
    }

    public void markDirtyClient() {
        markDirty();
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.checkLight(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, Constants.BlockFlags.DEFAULT);
        }
    }

    @Override
    public void setWorld(World worldIn) {
        super.setWorld(worldIn);
        this.positiveBlockInfo.setWorld(worldIn);
        this.negativeBlockInfo.setWorld(worldIn);
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        this.positiveBlockInfo.setPos(posIn);
        this.negativeBlockInfo.setPos(posIn);
    }

    @Override
    public IBlockInfo getPositiveBlockInfo() {
        return this.positiveBlockInfo;
    }

    @Override
    public IBlockInfo getNegativeBlockInfo() {
        return this.negativeBlockInfo;
    }

    @Override
    public void onLoad() {
        this.negativeBlockInfo.onLoad();
        this.positiveBlockInfo.onLoad();
    }

    @Override
    public void onChunkUnload() {
        this.negativeBlockInfo.onChunkUnloaded();
        this.positiveBlockInfo.onChunkUnloaded();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.negativeBlockInfo.remove();
        this.positiveBlockInfo.remove();
    }

    @Override
    public void validate() {
        super.validate();
        this.negativeBlockInfo.validate();
        this.positiveBlockInfo.validate();
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        this.negativeBlockInfo.updateContainingBlockInfo();
        this.positiveBlockInfo.updateContainingBlockInfo();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return this.negativeBlockInfo.hasCapability(capability, facing) || this.positiveBlockInfo.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (this.positiveBlockInfo.hasCapability(capability, facing))
            return this.positiveBlockInfo.getCapability(capability, facing);
        return this.negativeBlockInfo.getCapability(capability, facing);
    }

    @Override
    public void update() {
        if (this.world != null) {
            if (this.positiveBlockInfo.getTileEntity() != null && this.positiveBlockInfo.getTileEntity() instanceof ITickable) {
                if (this.positiveBlockInfo.getTileEntity().getWorld() == null)
                    this.positiveBlockInfo.getTileEntity().setWorld(this.positiveBlockInfo.getWorld());
                ((ITickable) this.positiveBlockInfo.getTileEntity()).update();
            }
            if (this.negativeBlockInfo.getTileEntity() != null && this.negativeBlockInfo.getTileEntity() instanceof ITickable) {
                if (this.negativeBlockInfo.getTileEntity().getWorld() == null)
                    this.negativeBlockInfo.getTileEntity().setWorld(this.negativeBlockInfo.getWorld());
                ((ITickable) this.negativeBlockInfo.getTileEntity()).update();
            }
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
