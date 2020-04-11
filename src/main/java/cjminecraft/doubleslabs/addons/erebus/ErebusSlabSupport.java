package cjminecraft.doubleslabs.addons.erebus;

import cjminecraft.doubleslabs.api.ISlabSupport;
import erebus.blocks.BlockSlabErebus;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ErebusSlabSupport implements ISlabSupport {
    @Override
    public boolean isValid(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() instanceof BlockSlabErebus && state.getValue(BlockSlabErebus.HALF) != BlockSlabErebus.EnumBlockHalf.FULL;
    }

    @Override
    public boolean isValid(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return ((ItemBlock) stack.getItem()).getBlock() instanceof BlockSlabErebus;
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return state.getValue(BlockSlabErebus.HALF) == BlockSlabErebus.EnumBlockHalf.BOTTOM ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, BlockSlab.EnumBlockHalf half) {
        return ((ItemBlock) stack.getItem()).getBlock().getDefaultState().withProperty(BlockSlabErebus.HALF, half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockSlabErebus.EnumBlockHalf.BOTTOM : BlockSlabErebus.EnumBlockHalf.TOP);
    }

    @Override
    public boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        return state.getBlock() == ((ItemBlock) stack.getItem()).getBlock();
    }
}
