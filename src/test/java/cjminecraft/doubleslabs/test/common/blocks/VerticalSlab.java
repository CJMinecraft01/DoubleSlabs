package cjminecraft.doubleslabs.test.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VerticalSlab extends Block implements IWaterLoggable {

    public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.create("type", VerticalSlabType.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public VerticalSlab(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(TYPE, VerticalSlabType.NORTH).with(WATERLOGGED, false));
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return state.get(TYPE) != VerticalSlabType.DOUBLE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE, WATERLOGGED);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(TYPE).shape;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        BlockState blockstate = context.getWorld().getBlockState(blockpos);
        if(blockstate.getBlock() == this)
            return blockstate.with(TYPE, VerticalSlabType.DOUBLE).with(WATERLOGGED, false);

        FluidState fluid = context.getWorld().getFluidState(blockpos);
        BlockState retState = getDefaultState().with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
        Direction direction = getDirectionForPlacement(context);
        VerticalSlabType type = VerticalSlabType.fromDirection(direction);

        return retState.with(TYPE, type);
    }

    private Direction getDirectionForPlacement(BlockItemUseContext context) {
        Direction direction = context.getFace();
        if(direction.getAxis() != Direction.Axis.Y)
            return direction;

        Vector3d vec = context.getHitVec().subtract(Vector3d.copy(context.getPos())).subtract(0.5, 0, 0.5);
        double angle = Math.atan2(vec.x, vec.z) * -180.0 / Math.PI;
        return Direction.fromAngle(angle).getOpposite();
    }

    @Override
    public boolean isReplaceable(BlockState state, @Nonnull BlockItemUseContext useContext) {
        ItemStack itemstack = useContext.getItem();
        VerticalSlabType slabtype = state.get(TYPE);
        return slabtype != VerticalSlabType.DOUBLE && itemstack.getItem() == this.asItem() && useContext.replacingClickedOnBlock() &&
                (useContext.getFace() == slabtype.direction && getDirectionForPlacement(useContext) == slabtype.direction);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean receiveFluid(@Nonnull IWorld worldIn, @Nonnull BlockPos pos, BlockState state, @Nonnull FluidState fluidStateIn) {
        return state.get(TYPE) != VerticalSlabType.DOUBLE && IWaterLoggable.super.receiveFluid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return state.get(TYPE) != VerticalSlabType.DOUBLE && IWaterLoggable.super.canContainFluid(worldIn, pos, state, fluidIn);
    }

    @Nonnull
    @Override
    public BlockState updatePostPlacement(@Nonnull BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if(stateIn.get(WATERLOGGED))
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean allowsMovement(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, PathType type) {
        return type == PathType.WATER && worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    public enum VerticalSlabType implements IStringSerializable {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        WEST(Direction.WEST),
        EAST(Direction.EAST),
        DOUBLE(null);

        private final String name;
        public final Direction direction;
        public final VoxelShape shape;

        VerticalSlabType(Direction direction) {
            this.name = direction == null ? "double" : direction.getString();
            this.direction = direction;

            if(direction == null)
                shape = VoxelShapes.fullCube();
            else {
                double min = 0;
                double max = 8;
                if(direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                    min = 8;
                    max = 16;
                }

                if(direction.getAxis() == Direction.Axis.X)
                    shape = Block.makeCuboidShape(min, 0, 0, max, 16, 16);
                else shape = Block.makeCuboidShape(0, 0, min, 16, 16, max);
            }
        }

        @Override
        public String toString() {
            return name;
        }

        public static VerticalSlabType fromDirection(Direction direction) {
            for(VerticalSlabType type : VerticalSlabType.values())
                if(type.direction != null && direction == type.direction)
                    return type;

            return null;
        }

        @Override
        public String getString() {
            return name;
        }
    }

}
