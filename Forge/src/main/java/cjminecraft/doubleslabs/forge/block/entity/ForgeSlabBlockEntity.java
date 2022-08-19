package cjminecraft.doubleslabs.forge.block.entity;

import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.forge.api.ForgeBlockInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ForgeSlabBlockEntity extends SlabBlockEntity<ForgeBlockInfo> {

    // I don't think persistent data is needed
    // todo: forge persistent data

    // todo: forge get model data

    public ForgeSlabBlockEntity(BlockPos pos, BlockState state, ForgeBlockInfo negativeBlockInfo, ForgeBlockInfo positiveBlockInfo) {
        super(pos, state, negativeBlockInfo, positiveBlockInfo);
    }

    @Override
    public void markDirtyClient() {
        setChanged();
        requestModelDataUpdate();
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            this.level.getLightEngine().checkBlock(this.worldPosition);
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        deserializeNBT(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        deserializeNBT(pkt.getTag());
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

    @Override
    public @NotNull ModelData getModelData() {
        // todo
        return super.getModelData();
    }
}
