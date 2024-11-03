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

    void callOnBlockStates(Consumer<BlockState> consumer);

    void callOnBlockStates(BiConsumer<Half, BlockState> consumer);

    void callOnBlockState(Half half, Consumer<BlockState> consumer);

    <T> T callOnBlockState(Half half, Function<BlockState, T> function, Supplier<T> orElse);

    void callOnBlockEntities(Consumer<BlockEntity> consumer);

    void callOnBlockEntities(BiConsumer<Half, BlockEntity> consumer);

    void callOnBlockEntity(Half half, Consumer<BlockEntity> consumer);

    <T> T callOnBlockEntity(Half half, Function<BlockEntity, T> function, Supplier<T> orElse);

}
