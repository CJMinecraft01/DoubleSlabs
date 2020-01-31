package cjminecraft.doubleslabs.tileentitiy;

import cjminecraft.doubleslabs.Registrar;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDoubleSlab extends TileEntity {

    public static final ModelProperty<BlockState> TOP_STATE = new ModelProperty<>();
    public static final ModelProperty<BlockState> BOTTOM_STATE = new ModelProperty<>();

    private BlockState topState;
    private BlockState bottomState;

    public TileEntityDoubleSlab() {
        super(Registrar.TILE_DOUBLE_SLAB);
        this.topState = Blocks.PURPUR_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP);
        this.bottomState = Blocks.ACACIA_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM);
    }

//    public TileEntityDoubleSlab(BlockState topState, BlockState bottomState) {
//        super(Registrar.TILE_DOUBLE_SLAB);
//        this.topState = topState;
//        this.bottomState = bottomState;
//    }

    public BlockState getTopState() {
        return topState;
    }

    public BlockState getBottomState() {
        return bottomState;
    }

    public TileEntityDoubleSlab setTopState(BlockState topState) {
        this.topState = topState;
        markDirtyClient();
        return this;
    }

    public TileEntityDoubleSlab setBottomState(BlockState bottomState) {
        this.bottomState = bottomState;
        markDirtyClient();
        return this;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("top", NBTUtil.writeBlockState(this.topState));
        nbt.put("bottom", NBTUtil.writeBlockState(this.bottomState));
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT read) {
        super.read(read);
        this.topState = NBTUtil.readBlockState(read.getCompound("top"));
        this.bottomState = NBTUtil.readBlockState(read.getCompound("bottom"));
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
        return new SUpdateTileEntityPacket(getPos(), 1, nbt);
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        super.handleUpdateTag(tag);
        this.read(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        BlockState oldTopState = this.topState;
        BlockState oldBottomState = this.bottomState;
        this.read(pkt.getNbtCompound());
        if (this.world.isRemote)
            if (oldTopState != this.topState || oldBottomState != this.bottomState)
                this.world.markChunkDirty(getPos(), getTileEntity());
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(BOTTOM_STATE, this.bottomState).withInitial(TOP_STATE, this.topState).build();
    }

    private void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            BlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public CompoundNBT getTileData() {
        return this.write(new CompoundNBT());
    }

}
