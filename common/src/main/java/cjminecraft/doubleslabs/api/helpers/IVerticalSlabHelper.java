package cjminecraft.doubleslabs.api.helpers;

import cjminecraft.doubleslabs.api.state.VerticalSlabState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IVerticalSlabHelper {

    default boolean isVerticalSlab(Item item) {
        return item instanceof BlockItem blockItem && isVerticalSlab(blockItem.getBlock());
    }

    boolean isVerticalSlab(Block block);

    default boolean isVerticalSlab(BlockState state) {
        return isVerticalSlab(state.getBlock());
    }

    default boolean isVerticalSlab(ItemStack stack) {
        return isVerticalSlab(stack.getItem());
    }

    VerticalSlabState getVerticalSlabState(BlockGetter level, BlockPos pos, BlockState state);

    BlockState getStateForVerticalSlabState(BlockGetter level, BlockPos pos, BlockState state, VerticalSlabState verticalSlabState);

    default boolean areSameTypeOfSlab(BlockState state, ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && state.is(blockItem.getBlock());
    }

    @Nullable
    default BlockState getStateFromStack(ItemStack stack, BlockPlaceContext blockPlaceContext) {
        return stack.getItem() instanceof BlockItem blockItem ?
                blockItem.getBlock().getStateForPlacement(blockPlaceContext) : null;
    }

}
