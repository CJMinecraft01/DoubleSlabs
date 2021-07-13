package cjminecraft.doubleslabs.api.support.minecraft;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.common.util.Pair;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Map;

@SlabSupportProvider
public class MinecraftSlabSupport implements IHorizontalSlabSupport {

    private static final Map<IProperty<?>, Block> HALF_TO_DOUBLE = Maps.newHashMap();

    private boolean isValid(IBlockState state) {
        return (state.getBlock() instanceof BlockSlab && !((BlockSlab) state.getBlock()).isDouble() && hasEnumHalfProperty(state));
    }

    private boolean hasEnumHalfProperty(IBlockState state) {
        return state.getPropertyKeys().contains(BlockSlab.HALF);
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        boolean valid = isValid(block.getDefaultState());
        if (valid) {
            BlockSlab slab = (BlockSlab) block;
            if (slab.isDouble())
                HALF_TO_DOUBLE.put(slab.getVariantProperty(), slab);
            return true;
        }
        return false;
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

    protected static <T extends Comparable<T>> IBlockState makeState(IBlockState state, IProperty<T> property, Comparable<?> value) {
        if (property != null)
            return state.withProperty(property, (T) value);
        return state;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        if (half == null) {
            // We want the double state
            BlockSlab slab = (BlockSlab) state.getBlock();
            return makeState(HALF_TO_DOUBLE.getOrDefault(slab.getVariantProperty(), slab).getDefaultState(), slab.getVariantProperty(), state.getValue(slab.getVariantProperty()));
        }
        return state.withProperty(BlockSlab.HALF, half);
    }

    @Override
    public boolean useDoubleSlabModel(IBlockState state) {
        return state.getPropertyKeys().stream().noneMatch(property -> property.getValueClass() == EnumFacing.class);
    }
}
