package cjminecraft.doubleslabs.addons.libraryex;

import cjminecraft.doubleslabs.api.ISlabSupport;
import logictechcorp.libraryex.block.BlockModSlab;
import logictechcorp.libraryex.item.ItemBlockModSlab;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LibraryExSlabSupport implements ISlabSupport {
    @Override
    public boolean isValid(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() instanceof BlockModSlab && state.getValue(BlockModSlab.TYPE) != BlockModSlab.SlabType.DOUBLE;
    }

    @Override
    public boolean isValid(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return stack.getItem() instanceof ItemBlockModSlab;
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return state.getValue(BlockModSlab.TYPE) == BlockModSlab.SlabType.BOTTOM ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return state.withProperty(BlockModSlab.TYPE, half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockModSlab.SlabType.BOTTOM : BlockModSlab.SlabType.TOP);
    }

    @Override
    public boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        return state.getBlock() == ((ItemBlock) stack.getItem()).getBlock();
    }
}
