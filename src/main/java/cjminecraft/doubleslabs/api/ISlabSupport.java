package cjminecraft.doubleslabs.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISlabSupport {
    boolean isValid(World world, BlockPos pos, IBlockState state);

    boolean isValid(ItemStack stack, EntityPlayer player, EnumHand hand);

    BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state);

    IBlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, BlockSlab.EnumBlockHalf half);
}
