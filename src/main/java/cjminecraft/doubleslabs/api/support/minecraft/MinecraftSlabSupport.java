package cjminecraft.doubleslabs.api.support.minecraft;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SlabSupportProvider
public class MinecraftSlabSupport implements IHorizontalSlabSupport {

    private boolean isValid(IBlockState state) {
        return (state.getBlock() instanceof BlockSlab && !((BlockSlab) state.getBlock()).isDouble() && hasEnumHalfProperty(state));
    }

    private boolean hasEnumHalfProperty(IBlockState state) {
        return state.getPropertyKeys().contains(BlockSlab.HALF);
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        return isValid(block.getDefaultState());
    }

    @Override
    public boolean isHorizontalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        return isValid(state);
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return item instanceof ItemBlock && isValid(((ItemBlock) item).getBlock().getDefaultState());
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return state.getValue(BlockSlab.HALF);
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return state.withProperty(BlockSlab.HALF, half);
    }

    @Override
    public boolean useDoubleSlabModel(IBlockState state) {
        return state.getPropertyKeys().stream().noneMatch(property -> property.getValueClass() == EnumFacing.class);
    }
}
