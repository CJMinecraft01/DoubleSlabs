package cjminecraft.doubleslabs.common.block.entity;

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
import net.minecraft.world.level.block.state.properties.Half;

import javax.annotation.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DynamicSlabBlockEntity<S extends ISlabStateContainer> extends BlockEntity {
    protected final S negativeBlockInfo = createBlockStateContainer();
    protected final S positiveBlockInfo = createBlockStateContainer();

    public DynamicSlabBlockEntity(BlockPos pos, BlockState blockState) {
        super(DSBlockEntities.DYNAMIC_SLAB.get(), pos, blockState);
    }

    protected abstract S createBlockStateContainer();

    public void setBlockState(Half half, BlockState state) {
        switch (half) {
            case TOP -> positiveBlockInfo.setBlockState(state);
            case BOTTOM -> negativeBlockInfo.setBlockState(state);
        }
    }

    public void setBlockEntity(Half half, @Nullable BlockEntity blockEntity) {
        switch (half) {
            case TOP -> positiveBlockInfo.setBlockEntity(blockEntity);
            case BOTTOM -> negativeBlockInfo.setBlockEntity(blockEntity);
        }
    }

    public void callOnBlockStates(Consumer<BlockState> consumer) {
        positiveBlockInfo.callOnBlockState(consumer);
        negativeBlockInfo.callOnBlockState(consumer);
    }

    public void callOnBlockStates(BiConsumer<Half, BlockState> consumer) {
        positiveBlockInfo.callOnBlockState(state -> consumer.accept(Half.TOP, state));
        negativeBlockInfo.callOnBlockState(state -> consumer.accept(Half.BOTTOM, state));
    }

    public void callOnBlockState(Half half, Consumer<BlockState> consumer) {
        switch (half) {
            case TOP -> positiveBlockInfo.callOnBlockState(consumer);
            case BOTTOM -> negativeBlockInfo.callOnBlockState(consumer);
        }
    }

    public <T> T callOnBlockState(Half half, Function<BlockState, T> function, Supplier<T> orElse) {
        return switch (half) {
            case TOP -> positiveBlockInfo.callOnBlockState(function, orElse);
            case BOTTOM -> negativeBlockInfo.callOnBlockState(function, orElse);
        };
    }

    public void callOnBlockEntities(Consumer<BlockEntity> consumer) {
        positiveBlockInfo.callOnBlockEntity(consumer);
        negativeBlockInfo.callOnBlockEntity(consumer);
    }

    public void callOnBlockEntities(BiConsumer<Half, BlockEntity> consumer) {
        positiveBlockInfo.callOnBlockEntity(blockEntity -> consumer.accept(Half.TOP, blockEntity));
        negativeBlockInfo.callOnBlockEntity(blockEntity -> consumer.accept(Half.BOTTOM, blockEntity));
    }

    public void callOnBlockEntity(Half half, Consumer<BlockEntity> consumer) {
        switch (half) {
            case TOP -> positiveBlockInfo.callOnBlockEntity(consumer);
            case BOTTOM -> negativeBlockInfo.callOnBlockEntity(consumer);
        }
    }

    public <T> T callOnBlockEntity(Half half, Function<BlockEntity, T> function, Supplier<T> orElse) {
        return switch (half) {
            case TOP -> positiveBlockInfo.callOnBlockEntity(function, orElse);
            case BOTTOM -> negativeBlockInfo.callOnBlockEntity(function, orElse);
        };
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        positiveBlockInfo.setLevel(level);
        negativeBlockInfo.setLevel(level);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("positive", positiveBlockInfo.serialize(registries));
        tag.put("negative", negativeBlockInfo.serialize(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        positiveBlockInfo.deserialize(tag.getCompound("positive"), registries);
        negativeBlockInfo.deserialize(tag.getCompound("negative"), registries);
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
