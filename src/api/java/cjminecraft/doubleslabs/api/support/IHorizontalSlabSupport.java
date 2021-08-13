package cjminecraft.doubleslabs.api.support;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public interface IHorizontalSlabSupport extends ISlabSupport {

    default boolean isHorizontalSlab(BlockGetter world, BlockPos pos, BlockState state) {
        return isHorizontalSlab(state.getBlock());
    }

    default boolean isHorizontalSlab(ItemStack stack, Player player, InteractionHand hand) {
        return isHorizontalSlab(stack.getItem());
    }

    boolean isHorizontalSlab(Item item);

    boolean isHorizontalSlab(Block block);

    SlabType getHalf(BlockGetter world, BlockPos pos, BlockState state);

    BlockState getStateForHalf(BlockGetter world, BlockPos pos, BlockState state, SlabType half);

    default boolean canCraft(Item item) {
        return true;
    }
}
