package cjminecraft.doubleslabs.api.support.blockcarpentry;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

@SlabSupportProvider(modid = "blockcarpentry")
public class BlockCarpentrySlabSupport<T extends Enum<T> & IStringSerializable> implements IHorizontalSlabSupport, IVerticalSlabSupport {

    private final Class<?> slab;

    public BlockCarpentrySlabSupport() {
        Class<?> slab;
        try {
            slab = Class.forName("mod.pianomanu.blockcarpentry.block.SixWaySlabFrameBlock");
        } catch (ClassNotFoundException ignored) {
            slab = null;
        }
        this.slab = slab;
    }

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return isHorizontalSlab(state.getBlock()) && state.get(FACING).getAxis().isHorizontal();
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        return slab != null && slab.isAssignableFrom(block.getClass());
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return slab != null && item instanceof BlockItem && slab.isAssignableFrom(((BlockItem) item).getBlock().getClass());
    }

    @Override
    public SlabType getHalf(IBlockReader world, BlockPos pos, BlockState state) {
        return state.get(FACING) == Direction.DOWN ? SlabType.TOP : SlabType.BOTTOM;
    }

    @Override
    public BlockState getStateForHalf(IBlockReader world, BlockPos pos, BlockState state, SlabType half) {
        return state.with(FACING, half == SlabType.BOTTOM ? Direction.UP : Direction.DOWN);
    }

    @Override
    public boolean isVerticalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass()) && state.get(FACING).getAxis().isHorizontal();
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return slab != null && stack.getItem() instanceof BlockItem && slab.isAssignableFrom(((BlockItem) stack.getItem()).getBlock().getClass());
    }

    @Override
    public Direction getDirection(IBlockReader world, BlockPos pos, BlockState state) {
        return state.get(FACING).getOpposite();
    }

    @Override
    public BlockState getStateForDirection(IBlockReader world, BlockPos pos, BlockState state, Direction direction) {
        return state.with(FACING, direction.getOpposite());
    }

    @Override
    public boolean useDoubleSlabModel(BlockState state) {
        return false;
    }
}
