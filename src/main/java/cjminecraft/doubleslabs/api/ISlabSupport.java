package cjminecraft.doubleslabs.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ISlabSupport {
    boolean isValid(World world, BlockPos pos, IBlockState state);

    boolean isValid(ItemStack stack, EntityPlayer player, EnumHand hand);

    BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state);

    default IBlockState getStateFromStack(ItemStack stack, World world, BlockPos pos, EnumFacing facing, Vec3d hitVec, EntityPlayer player, EnumHand hand) {
        return Block.getBlockFromItem(stack.getItem()).getStateForPlacement(world, pos, facing, (float)hitVec.x, (float)hitVec.y, (float)hitVec.z, stack.getMetadata(), player, hand);
    }

    IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half);

    boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack);
}
