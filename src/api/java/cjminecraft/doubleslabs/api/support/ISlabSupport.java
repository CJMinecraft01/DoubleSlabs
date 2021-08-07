package cjminecraft.doubleslabs.api.support;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ISlabSupport {

    default boolean areSame(Level world, BlockPos pos, BlockState state, ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() == state.getBlock();
    }

    default InteractionResult onBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return state.use(world, player, hand, hit);
    }

    default BlockState getStateFromStack(ItemStack stack, BlockPlaceContext context) {
        return stack.getItem() instanceof BlockItem ? ((BlockItem) stack.getItem()).getBlock().getStateForPlacement(context) : Blocks.AIR.defaultBlockState();
    }

    default boolean useDoubleSlabModel(BlockState state) {
        return true;
    }

    default boolean waterloggableWhenDouble(Level world, BlockPos pos, BlockState state) {
        return false;
    }

    default boolean requiresWrappedWorld(BlockState state) {
        return state.hasBlockEntity() || state.getBlock() instanceof ChangeOverTimeBlock<?>;
    }

}
