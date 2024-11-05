package cjminecraft.doubleslabs.api.state;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IDynamicSlabStateContainer {

    void setBlockState(Half half, BlockState state);

    void setBlockEntity(Half half, @Nullable BlockEntity blockEntity);

    ISlabStateContainer getStateContainer(Half half);

    void runOnStateContainers(Consumer<ISlabStateContainer> consumer);

    void runOnStateContainers(BiConsumer<Half, ISlabStateContainer> consumer);

    void runOnStateContainer(Half half, Consumer<ISlabStateContainer> consumer);

    <T> T callOnStateContainer(Half half, Function<ISlabStateContainer, T> consumer);

    default void runOnBlockStates(Consumer<BlockState> consumer) {
        runOnStateContainers(container -> container.runOnBlockState(consumer));
    }

    default void runOnBlockStates(BiConsumer<Half, BlockState> consumer) {
        runOnStateContainers((half, container) -> container.runOnBlockState(state -> consumer.accept(half, state)));
    }

    default void runOnBlockState(Half half, Consumer<BlockState> consumer) {
        runOnStateContainer(half, container -> container.runOnBlockState(consumer));
    }

    default <T> T callOnBlockState(Half half, Function<BlockState, T> function, Supplier<T> orElse) {
        return callOnStateContainer(half, container -> container.callOnBlockState(function, orElse));
    }

    default void runOnBlockEntities(Consumer<BlockEntity> consumer) {
        runOnStateContainers(container -> container.runOnBlockEntity(consumer));
    }

    default void runOnBlockEntities(BiConsumer<Half, BlockEntity> consumer) {
        runOnStateContainers((half, container) -> container.runOnBlockEntity(blockEntity -> consumer.accept(half,
                blockEntity)));
    }

    default void runOnBlockEntity(Half half, Consumer<BlockEntity> consumer) {
        runOnStateContainer(half, container -> container.runOnBlockEntity(consumer));
    }

    default <T> T callOnBlockEntity(Half half, Function<BlockEntity, T> function, Supplier<T> orElse) {
        return callOnStateContainer(half, container -> container.callOnBlockEntity(function, orElse));
    }

}
