package cjminecraft.doubleslabs.common.block.entity;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class DynamicSlabBlockEntity<S extends ISlabStateContainer> extends BlockEntity implements IDynamicSlabStateContainer {
    protected final S negativeBlockStateContainer = createBlockStateContainer();
    protected final S positiveBlockStateContainer = createBlockStateContainer();

    public DynamicSlabBlockEntity(BlockPos pos, BlockState blockState) {
        super(DSBlockEntities.DYNAMIC_SLAB.get(), pos, blockState);
    }

    protected abstract S createBlockStateContainer();

    @Override
    public void markDirty() {
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            this.level.getLightEngine().checkBlock(this.worldPosition);
        }
    }

    public void setBlockState(Half half, BlockState state) {
        switch (half) {
            case TOP -> positiveBlockStateContainer.setBlockState(state);
            case BOTTOM -> negativeBlockStateContainer.setBlockState(state);
        }
    }

    public void setBlockEntity(Half half, @Nullable BlockEntity blockEntity) {
        switch (half) {
            case TOP -> positiveBlockStateContainer.setBlockEntity(blockEntity);
            case BOTTOM -> negativeBlockStateContainer.setBlockEntity(blockEntity);
        }
    }

    @Override
    public ISlabStateContainer getStateContainer(Half half) {
        return switch (half) {
            case TOP -> positiveBlockStateContainer;
            case BOTTOM -> negativeBlockStateContainer;
        };
    }

    @Override
    public void runOnStateContainers(Consumer<ISlabStateContainer> consumer) {
        consumer.accept(positiveBlockStateContainer);
        consumer.accept(negativeBlockStateContainer);
    }

    @Override
    public void runOnStateContainers(BiConsumer<Half, ISlabStateContainer> consumer) {
        consumer.accept(Half.TOP, positiveBlockStateContainer);
        consumer.accept(Half.BOTTOM, negativeBlockStateContainer);
    }

    @Override
    public void runOnStateContainer(Half half, Consumer<ISlabStateContainer> consumer) {
        switch (half) {
            case TOP -> consumer.accept(positiveBlockStateContainer);
            case BOTTOM -> consumer.accept(negativeBlockStateContainer);
        }
    }

    @Override
    public <T> T callOnStateContainer(Half half, Function<ISlabStateContainer, T> consumer) {
        return switch (half) {
            case TOP -> consumer.apply(positiveBlockStateContainer);
            case BOTTOM -> consumer.apply(negativeBlockStateContainer);
        };
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        positiveBlockStateContainer.setLevel(level);
        negativeBlockStateContainer.setLevel(level);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("positive", positiveBlockStateContainer.serialize(registries));
        tag.put("negative", negativeBlockStateContainer.serialize(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        positiveBlockStateContainer.deserialize(tag.getCompound("positive"), registries);
        negativeBlockStateContainer.deserialize(tag.getCompound("negative"), registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
