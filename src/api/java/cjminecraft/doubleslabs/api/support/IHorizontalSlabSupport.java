package cjminecraft.doubleslabs.api.support;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public interface IHorizontalSlabSupport extends ISlabSupport {

    default boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return isHorizontalSlab(state.getBlock());
    }

    default boolean isHorizontalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return isHorizontalSlab(stack.getItem());
    }

    boolean isHorizontalSlab(Item item);

    boolean isHorizontalSlab(Block block);

    SlabType getHalf(World world, BlockPos pos, BlockState state);

    BlockState getStateForHalf(World world, BlockPos pos, BlockState state, SlabType half);

}
