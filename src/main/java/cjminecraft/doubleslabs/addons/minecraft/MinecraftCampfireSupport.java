package cjminecraft.doubleslabs.addons.minecraft;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class MinecraftCampfireSupport implements ISlabSupport {

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return state.getBlock() == Blocks.CAMPFIRE;
    }

    @Override
    public boolean isHorizontalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() == Blocks.CAMPFIRE;
    }

    @Override
    public SlabType getHalf(World world, BlockPos pos, BlockState state) {
        return SlabType.BOTTOM;
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, SlabType half) {
        BlockItem slab = (BlockItem) stack.getItem();
        return slab.getBlock().getDefaultState();
    }

    @Override
    public boolean areSame(World world, BlockPos pos, BlockState state, ItemStack stack) {
        return ((BlockItem) stack.getItem()).getBlock() == state.getBlock();
    }

    @Override
    public float getOffsetY(boolean positive) {
        return positive ? 0.5f : 0;
    }
}
