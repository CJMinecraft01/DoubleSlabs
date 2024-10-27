package cjminecraft.doubleslabs.api.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public interface IHorizontalSlabHelper {

    default boolean isHorizontalSlab(Item item) {
        return item instanceof BlockItem blockItem && isHorizontalSlab(blockItem.getBlock());
    }

    boolean isHorizontalSlab(Block block);

    default boolean isHorizontalSlab(BlockGetter level, BlockPos pos, BlockState state) {
        return isHorizontalSlab(state.getBlock());
    }

    default boolean isHorizontalSlab(ItemStack stack, Player player, InteractionHand hand) {
        return isHorizontalSlab(stack.getItem());
    }

    SlabType getHalf(BlockGetter level, BlockPos pos, BlockState state);

    BlockState getStateForHalf(BlockGetter level, BlockPos pos, BlockState state, SlabType half);

}
