package cjminecraft.doubleslabs.api.support.lotr;

import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.minecraft.MinecraftSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SlabSupportProvider(modid = "lotr")
public class LotRSlabSupport extends MinecraftSlabSupport implements IVerticalSlabSupport {

    public static boolean isLotRSlab(BlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass()) && state.has(BlockStateProperties.SLAB_TYPE) && state.has(BlockStateProperties.AXIS);
    }

    private static final Class<?> slab;

    static {
        Class<?> slab1;
        try {
            slab1 = Class.forName("lotr.common.block.AxialSlabBlock");
        } catch(ClassNotFoundException ignored) {
            slab1 = null;
        }
        slab = slab1;
    }

    private boolean isValid(BlockState state) {
        return slab != null && isLotRSlab(state) && state.get(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE;
    }

    @Override
    public boolean isHorizontalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return false;
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return slab != null && item instanceof BlockItem && isValid(((BlockItem) item).getBlock().getDefaultState());
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, BlockState state, SlabType half) {
        return super.getStateForHalf(world, pos, state, half).with(BlockStateProperties.AXIS, Direction.Axis.Y);
    }

    @Override
    public boolean isVerticalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return isValid(state) && state.get(BlockStateProperties.AXIS).isHorizontal();
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return isHorizontalSlab(stack.getItem());
    }

    @Override
    public Direction getDirection(World world, BlockPos pos, BlockState state) {
        boolean top = state.get(SlabBlock.TYPE) == SlabType.TOP;
        Direction.Axis axis = state.get(BlockStateProperties.AXIS);
        if (axis == Direction.Axis.X)
            return top ? Direction.EAST : Direction.WEST;
        return top ? Direction.WEST : Direction.EAST;
    }

    @Override
    public BlockState getStateForDirection(World world, BlockPos pos, BlockState state, Direction direction) {
        return state.with(BlockStateProperties.AXIS, direction.getAxis()).with(SlabBlock.TYPE, direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? SlabType.BOTTOM : SlabType.TOP);
    }

    @Override
    public boolean rotateModel(IBlockReader world, BlockPos pos, BlockState state) {
        return !state.get(BlockStateProperties.AXIS).isHorizontal();
    }
}
