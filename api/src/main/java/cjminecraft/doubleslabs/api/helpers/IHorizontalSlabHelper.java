package cjminecraft.doubleslabs.api.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

public interface IHorizontalSlabHelper {

    default boolean isHorizontalSlab(Item item) {
        return item instanceof BlockItem blockItem && isHorizontalSlab(blockItem.getBlock());
    }

    boolean isHorizontalSlab(Block block);

    default boolean isHorizontalSlab(BlockState state) {
        return isHorizontalSlab(state.getBlock());
    }

    default boolean isHorizontalSlab(ItemStack stack) {
        return isHorizontalSlab(stack.getItem());
    }

    Half getHalf(BlockGetter level, BlockPos pos, BlockState state);

    BlockState getStateForHalf(BlockGetter level, BlockPos pos, BlockState state, Half half);

    boolean areSameTypeOfSlab(BlockState state, ItemStack stack);

}
