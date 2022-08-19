package cjminecraft.doubleslabs.common.block.entity;

import cjminecraft.doubleslabs.api.BlockInfo;
import cjminecraft.doubleslabs.api.ILevelWrapper;
import cjminecraft.doubleslabs.api.IStateContainer;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class SlabBlockEntity<T extends BlockInfo> extends BlockEntity implements IStateContainer {
    protected final T negativeBlockInfo;
    protected final T positiveBlockInfo;

    public SlabBlockEntity(BlockPos pos, BlockState state, T negativeBlockInfo, T positiveBlockInfo) {
        super(DSBlockEntities.DYNAMIC_SLAB.get(), pos, state);
        this.negativeBlockInfo = negativeBlockInfo;
        this.positiveBlockInfo = positiveBlockInfo;
    }

    public abstract void markDirtyClient();

    @Override
    public void load(CompoundTag tag) {
        negativeBlockInfo.deserializeNBT(tag.getCompound("negativeBlock"));
        positiveBlockInfo.deserializeNBT(tag.getCompound("positiveBlock"));
        super.load(tag);
        markDirtyClient();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("negativeBlock", this.negativeBlockInfo.serializeNBT());
        tag.put("positiveBlock", this.positiveBlockInfo.serializeNBT());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void markDirty() {
        markDirtyClient();
    }

    @Override
    public void setLevel(Level level) {
        if (level instanceof ILevelWrapper<?>)
            return;
        super.setLevel(level);
        negativeBlockInfo.setLevel(level);
        positiveBlockInfo.setLevel(level);
    }

    @Override
    public BlockInfo getPositiveBlockInfo() {
        return positiveBlockInfo;
    }

    @Override
    public BlockInfo getNegativeBlockInfo() {
        return negativeBlockInfo;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        negativeBlockInfo.setRemoved();
        positiveBlockInfo.setRemoved();
    }

    @Override
    public boolean triggerEvent(int pA, int pB) {
        return this.negativeBlockInfo.triggerEvent(pA, pB) | this.positiveBlockInfo.triggerEvent(pA, pB);
    }

    @SuppressWarnings("unchecked")
    public static <E extends SlabBlockEntity, A extends BlockEntity, B extends BlockEntity> void tick(Level world, BlockPos blockPos, BlockState blockState, E entity) {
        if (world != null) {
            if (entity.positiveBlockInfo.getBlockEntity() != null && entity.positiveBlockInfo.getBlockState() != null) {
                BlockEntityTicker<A> ticker = (BlockEntityTicker<A>) entity.positiveBlockInfo.getBlockState().getTicker(entity.getLevel(), entity.positiveBlockInfo.getBlockEntity().getType());
                if (ticker != null) {
                    if (entity.positiveBlockInfo.getBlockEntity().getLevel() == null)
                        entity.positiveBlockInfo.getBlockEntity().setLevel(entity.positiveBlockInfo.getLevel());
                    ticker.tick(entity.positiveBlockInfo.getLevel(), blockPos, entity.positiveBlockInfo.getBlockState(), (A) entity.positiveBlockInfo.getBlockEntity());
                }
            }
            if (entity.negativeBlockInfo.getBlockEntity() != null && entity.negativeBlockInfo.getBlockState() != null) {
                BlockEntityTicker<B> ticker = (BlockEntityTicker<B>) entity.negativeBlockInfo.getBlockState().getTicker(entity.getLevel(), entity.negativeBlockInfo.getBlockEntity().getType());
                if (ticker != null) {
                    if (entity.negativeBlockInfo.getBlockEntity().getLevel() == null)
                        entity.negativeBlockInfo.getBlockEntity().setLevel(entity.negativeBlockInfo.getLevel());
                    ticker.tick(entity.negativeBlockInfo.getLevel(), blockPos, entity.negativeBlockInfo.getBlockState(), (B) entity.negativeBlockInfo.getBlockEntity());
                }
            }
        }
    }
}
