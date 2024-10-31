package cjminecraft.doubleslabs.api.state;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ISlabStateContainer {

    BlockState getBlockState();

    void setBlockState(BlockState state);

    default boolean hasBlockState() {
        return !getBlockState().isAir();
    }

    default void callOnBlockState(Consumer<BlockState> consumer) {
        if (hasBlockState()) {
            consumer.accept(getBlockState());
        }
    }

    default <T> T callOnBlockState(Function<BlockState, T> function, Supplier<T> orElse) {
        return hasBlockState() ? function.apply(getBlockState()) : orElse.get();
    }

    @Nullable
    BlockEntity getBlockEntity();

    void setBlockEntity(@Nullable BlockEntity blockEntity);

    default boolean hasBlockEntity() {
        return getBlockEntity() != null;
    }

    default void callOnBlockEntity(Consumer<BlockEntity> consumer) {
        if (hasBlockEntity()) {
            consumer.accept(getBlockEntity());
        }
    }

    default <T> T callOnBlockEntity(Function<BlockEntity, T> function, Supplier<T> orElse) {
        return hasBlockEntity() ? function.apply(getBlockEntity()) : orElse.get();
    }

}
