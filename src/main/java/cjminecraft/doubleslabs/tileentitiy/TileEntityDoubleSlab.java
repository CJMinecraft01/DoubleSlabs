package cjminecraft.doubleslabs.tileentitiy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDoubleSlab extends TileEntity {

    private IBlockState topState;
    private IBlockState bottomState;

    public TileEntityDoubleSlab() {}

    public TileEntityDoubleSlab(IBlockState topState, IBlockState bottomState) {
        this.topState = topState;
        this.bottomState = bottomState;
    }

    public IBlockState getTopState() {
        return topState;
    }

    public IBlockState getBottomState() {
        return bottomState;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (this.topState != null)
            nbt.setTag("top", NBTUtil.writeBlockState(new NBTTagCompound(), this.topState));
        if (this.bottomState != null)
            nbt.setTag("bottom", NBTUtil.writeBlockState(new NBTTagCompound(), this.bottomState));
        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("top"))
            this.topState = NBTUtil.readBlockState(nbt.getCompoundTag("top"));
        if (nbt.hasKey("bottom"))
            this.bottomState = NBTUtil.readBlockState(nbt.getCompoundTag("bottom"));
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

}
