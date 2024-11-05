package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.Optional;
import java.util.function.Function;

public class DynamicSlabHooks {

    protected static Optional<? extends IDynamicSlabStateContainer> getDynamicSlabStateContainer(BlockGetter blockGetter, BlockPos pos) {
        return blockGetter.getBlockEntity(pos, DSBlockEntities.DYNAMIC_SLAB.get());
    }

    protected static Optional<Float> minFromBlockState(BlockGetter blockGetter, BlockPos pos, Function<BlockState,
            Float> function) {
        return getDynamicSlabStateContainer(blockGetter, pos).flatMap(container -> container.reduceOnBlockStates(function, Math::min));
    }

    protected static <T> Optional<T> callOnBlockState(BlockGetter blockGetter, BlockPos pos, Half slabHalf,
                                                      Function<BlockState, T> function) {
        return getDynamicSlabStateContainer(blockGetter, pos).flatMap(container -> container.callOnBlockState(slabHalf, function));
    }

}
