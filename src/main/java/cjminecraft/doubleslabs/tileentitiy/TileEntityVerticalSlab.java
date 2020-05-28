package cjminecraft.doubleslabs.tileentitiy;

import cjminecraft.doubleslabs.Registrar;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class TileEntityVerticalSlab extends TileEntity {
    
    private BlockState negativeState;
    private BlockState positiveState;
    
    public TileEntityVerticalSlab() {
        super(Registrar.TILE_VERTICAL_SLAB);
        negativeState = Blocks.PURPUR_SLAB.getDefaultState().with(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM);
        positiveState = Blocks.STONE_SLAB.getDefaultState().with(BlockStateProperties.SLAB_TYPE, SlabType.TOP);
    }

    public BlockState getNegativeState() {
        return this.negativeState;
    }

    public BlockState getPositiveState() {
        return this.positiveState;
    }

    public void setNegativeState(BlockState negativeState) {
        this.negativeState = negativeState;
        markDirtyClient();
    }

    public void setPositiveState(BlockState positiveState) {
        this.positiveState = positiveState;
        markDirtyClient();
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        if (this.negativeState != null)
            nbt.put("negative", NBTUtil.writeBlockState(this.negativeState));
        if (this.positiveState != null)
            nbt.put("positive", NBTUtil.writeBlockState(this.positiveState));
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT read) {
        super.read(read);
        if (read.contains("negative"))
            this.negativeState = NBTUtil.readBlockState(read.getCompound("negative"));
        if (read.contains("positive"))
            this.positiveState = NBTUtil.readBlockState(read.getCompound("positive"));
        markDirtyClient();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        this.write(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);
        return new SUpdateTileEntityPacket(getPos(), 0, nbt);
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        super.handleUpdateTag(tag);
        this.read(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        BlockState oldTopState = this.negativeState;
        BlockState oldBottomState = this.positiveState;
        this.read(pkt.getNbtCompound());
        if (this.world.isRemote)
            if (oldTopState != this.negativeState || oldBottomState != this.positiveState)
                this.world.markChunkDirty(getPos(), getTileEntity());
    }

    private void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            BlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }
}
