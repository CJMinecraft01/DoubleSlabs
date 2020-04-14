package cjminecraft.doubleslabs.addons.thebetweenlands;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;
import thebetweenlands.common.item.ItemBlockSlab;

public class TheBetweenlandsSlabSupport implements ISlabSupport {
    @Override
    public boolean isValid(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() instanceof BlockSlabBetweenlands && state.getValue(BlockSlabBetweenlands.HALF) != BlockSlabBetweenlands.EnumBlockHalfBL.FULL;
    }

    @Override
    public boolean isValid(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return stack.getItem() instanceof ItemBlockSlab;
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return state.getValue(BlockSlabBetweenlands.HALF) == BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, BlockSlab.EnumBlockHalf half) {
        return ((ItemBlock) stack.getItem()).getBlock().getDefaultState().withProperty(BlockSlabBetweenlands.HALF, half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM : BlockSlabBetweenlands.EnumBlockHalfBL.TOP);
    }

    @Override
    public boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        return ((ItemBlock) stack.getItem()).getBlock() == state.getBlock();
    }
}
