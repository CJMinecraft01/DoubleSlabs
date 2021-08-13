package cjminecraft.doubleslabs.api.support;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public interface ISlabSupport {

    default boolean areSame(World world, BlockPos pos, BlockState state, ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() == state.getBlock();
    }

    default ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return state.onBlockActivated(world, player, hand, hit);
    }

    default BlockState getStateFromStack(ItemStack stack, BlockItemUseContext context) {
        return stack.getItem() instanceof BlockItem ? ((BlockItem) stack.getItem()).getBlock().getStateForPlacement(context) : Blocks.AIR.getDefaultState();
    }

    default boolean useDoubleSlabModel(BlockState state) {
        return true;
    }

    default boolean waterloggableWhenDouble(World world, BlockPos pos, BlockState state) {
        return false;
    }

    default boolean shouldCull(BlockState currentState, BlockState otherState) {
        return true;
    }

    default boolean uvlock(Block block) {
        return true;
    }

}
