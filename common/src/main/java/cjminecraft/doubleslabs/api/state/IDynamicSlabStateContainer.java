package cjminecraft.doubleslabs.api.state;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.*;

public interface IDynamicSlabStateContainer {

    void markDirty();

    @Nullable Half getMissingHalf();

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

    default <T> Optional<T> reduceOnBlockStates(Function<BlockState, T> consumer, BiFunction<T, T, T> reducer) {
        final var resultTop = callOnBlockState(Half.POSITIVE, consumer);
        final var resultBottom = callOnBlockState(Half.NEGATIVE, consumer);

        if (resultTop.isPresent() && resultBottom.isPresent()) {
            return Optional.of(reducer.apply(resultTop.get(), resultBottom.get()));
        }

        return resultTop.or(() -> resultBottom);
    }

    default void runOnBlockState(Half half, Consumer<BlockState> consumer) {
        runOnStateContainer(half, container -> container.runOnBlockState(consumer));
    }

    default <T> T callOnBlockState(Half half, Function<BlockState, T> function, Supplier<T> orElse) {
        return callOnStateContainer(half, container -> container.callOnBlockState(function, orElse));
    }

    default <T> Optional<T> callOnBlockState(Half half, Function<BlockState, T> function) {
        return callOnStateContainer(half, container -> container.callOnBlockState(function));
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

    default <T> Optional<T> callOnBlockEntity(Half half, Function<BlockEntity, T> function) {
        return callOnStateContainer(half, container -> container.callOnBlockEntity(function));
    }

}
