package cjminecraft.doubleslabs.api.support.lotr;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@SlabSupportProvider(modid = "lotr", priority = 1)
public class LotrSlabSupport implements IHorizontalSlabSupport, IVerticalSlabSupport {

    private final Class<?> slab;
    private final EnumProperty<Direction.Axis> slabAxisProperty;

    public LotrSlabSupport() {
        Class<?> slab;
        EnumProperty<Direction.Axis> slabAxisProperty;
        try {
            slab = Class.forName("lotr.common.block.AxialSlabBlock");
            slabAxisProperty = (EnumProperty<Direction.Axis>) Class.forName("lotr.common.block.LOTRBlockStates").getField("SLAB_AXIS").get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            slab = null;
            slabAxisProperty = null;
        }
        this.slab = slab;
        this.slabAxisProperty = slabAxisProperty;
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return slab != null && item instanceof BlockItem && slab.isAssignableFrom(((BlockItem) item).getBlock().getClass());
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        return false;
    }

    @Override
    public SlabType getHalf(IBlockReader world, BlockPos pos, BlockState state) {
        return slab != null ? null : state.get(BlockStateProperties.SLAB_TYPE);
    }

    @Override
    public BlockState getStateForHalf(BlockState state, SlabType half) {
        return state.with(slabAxisProperty, Direction.Axis.Y).with(BlockStateProperties.SLAB_TYPE, half);
    }

    @Override
    public boolean isVerticalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass()) && state.get(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE && state.get(slabAxisProperty) != Direction.Axis.Y;
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return false;
//        return slab != null && (stack.getItem() instanceof BlockItem) && slab.isAssignableFrom(((BlockItem) stack.getItem()).getBlock().getClass());
    }

    @Override
    public Direction getDirection(IBlockReader world, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.get(slabAxisProperty);
        SlabType type = state.get(BlockStateProperties.SLAB_TYPE);
        switch (axis) {
            case X:
                return type == SlabType.BOTTOM ? Direction.NORTH : Direction.SOUTH;
            case Z:
                return type == SlabType.BOTTOM ? Direction.EAST : Direction.WEST;
        }
        return null;
    }

    @Override
    public BlockState getStateForDirection(IBlockReader world, BlockPos pos, BlockState state, Direction direction) {
        switch (direction.getAxis()) {
            case X:
                return state.with(slabAxisProperty, Direction.Axis.X).with(BlockStateProperties.SLAB_TYPE, direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? SlabType.BOTTOM : SlabType.TOP);
            case Z:
                return state.with(slabAxisProperty, Direction.Axis.Z).with(BlockStateProperties.SLAB_TYPE, direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? SlabType.TOP : SlabType.BOTTOM);
        }
        return state;
    }
}
