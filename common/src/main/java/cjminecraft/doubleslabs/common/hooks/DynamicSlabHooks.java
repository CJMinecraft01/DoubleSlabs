package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DynamicSlabHooks {

    protected static Optional<? extends IDynamicSlabStateContainer> getDynamicSlabStateContainer(BlockGetter blockGetter, BlockPos pos) {
        return blockGetter.getBlockEntity(pos, DSBlockEntities.DYNAMIC_SLAB.get());
    }

    protected static Optional<Float> minFromBlockState(BlockGetter blockGetter, BlockPos pos, Function<BlockState,
            Float> function) {
        return getDynamicSlabStateContainer(blockGetter, pos).flatMap(container -> container.reduceOnBlockStates(function, Math::min));
    }

    protected static void runOnBlockState(BlockGetter blockGetter, BlockPos pos, Half slabHalf,
                                                      Consumer<BlockState> consumer) {
        getDynamicSlabStateContainer(blockGetter, pos).ifPresent(container -> container.runOnBlockState(slabHalf, consumer));
    }

    protected static <T> Optional<T> callOnBlockState(BlockGetter blockGetter, BlockPos pos, Half slabHalf,
                                                      Function<BlockState, T> function) {
        return getDynamicSlabStateContainer(blockGetter, pos).flatMap(container -> container.callOnBlockState(slabHalf, function));
    }

    public static List<ItemStack> getDrops(LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();

        BlockEntity blockEntity = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof DynamicSlabBlockEntity<?> dynamicSlab) {
            dynamicSlab.runOnStateContainers(container -> {
                if (!container.hasBlockState()) {
                    return;
                }

                BlockState slabState = container.getBlockState();

                LootParams.Builder slabParams = params.withParameter(LootContextParams.BLOCK_STATE, slabState);

                if (container.hasBlockEntity()) {
                    slabParams = slabParams.withParameter(LootContextParams.BLOCK_ENTITY, Objects.requireNonNull(container.getBlockEntity()));
                }

                drops.addAll(slabState.getDrops(slabParams));
            });
        }

        return drops;
    }

}
