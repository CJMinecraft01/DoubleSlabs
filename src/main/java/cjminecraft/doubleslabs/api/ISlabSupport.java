package cjminecraft.doubleslabs.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISlabSupport {
    boolean isValid(World world, BlockPos pos, BlockState state);

    boolean isValid(ItemStack stack, PlayerEntity player, Hand hand);

    SlabType getHalf(World world, BlockPos pos, BlockState state);

    BlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, SlabType half);

    boolean areSame(World world, BlockPos pos, BlockState state, ItemStack stack);
}
