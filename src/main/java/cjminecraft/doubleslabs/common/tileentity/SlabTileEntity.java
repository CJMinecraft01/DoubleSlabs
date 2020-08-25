package cjminecraft.doubleslabs.common.tileentity;

import cjminecraft.doubleslabs.api.BlockInfo;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import cjminecraft.doubleslabs.common.init.DSTiles;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlabTileEntity extends TileEntity implements IStateContainer {

    protected final BlockInfo negativeBlockInfo = new BlockInfo(this, false);
    protected final BlockInfo positiveBlockInfo = new BlockInfo(this, true);

    public SlabTileEntity() {
        super(DSTiles.DYNAMIC_SLAB.get());
    }

    protected SlabTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        this.negativeBlockInfo.deserializeNBT(nbt.getCompound("negativeBlock"));
        this.positiveBlockInfo.deserializeNBT(nbt.getCompound("positiveBlock"));
        super.read(state, nbt);
        markDirtyClient();
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("negativeBlock", this.negativeBlockInfo.serializeNBT());
        nbt.put("positiveBlock", this.positiveBlockInfo.serializeNBT());
        return super.write(nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        this.write(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        this.read(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);
        return new SUpdateTileEntityPacket(getPos(), -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        this.read(this.world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getTileData() {
        return this.write(new CompoundNBT());
    }

    protected void markDirtyFast() {
        if (this.world != null)
            this.world.markChunkDirty(this.pos, this);
    }

    public void markDirtyClient() {
        markDirty();
        requestModelDataUpdate();
        if (this.world != null) {
            BlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, Constants.BlockFlags.DEFAULT);
            this.world.getLightManager().checkBlock(this.pos);
        }
    }

    @Override
    public void setWorldAndPos(World world, BlockPos pos) {
        super.setWorldAndPos(world, pos);
        this.negativeBlockInfo.setWorld(world);
        this.positiveBlockInfo.setWorld(world);
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
    public void onChunkUnloaded() {
        this.negativeBlockInfo.onChunkUnloaded();
        this.positiveBlockInfo.onChunkUnloaded();
    }

    @Override
    public void remove() {
        super.remove();
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        LazyOptional<T> negativeCapability = this.negativeBlockInfo.getCapability(cap);
        return negativeCapability.isPresent() ? negativeCapability : this.positiveBlockInfo.getCapability(cap);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        LazyOptional<T> negativeCapability = this.negativeBlockInfo.getCapability(cap, side);
        return negativeCapability.isPresent() ? negativeCapability : this.positiveBlockInfo.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(DynamicSlabBakedModel.NEGATIVE_BLOCK, this.negativeBlockInfo).withInitial(DynamicSlabBakedModel.POSITIVE_BLOCK, this.positiveBlockInfo).build();
    }
}
