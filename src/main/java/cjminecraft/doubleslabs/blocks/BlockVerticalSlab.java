package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockVerticalSlab extends Block implements IWaterLoggable {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockVerticalSlab() {
        super(Properties.create(Material.ROCK).notSolid());
        setRegistryName(DoubleSlabs.MODID, "vertical_slab");
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, DOUBLE, WATERLOGGED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Registrar.TILE_VERTICAL_SLAB.create();
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return !state.get(DOUBLE);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (state.get(DOUBLE))
            return VoxelShapes.fullCube();
        double min = 0;
        double max = 8;
        if (state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            min = 8;
            max = 16;
        }

        if (state.get(FACING).getAxis() == Direction.Axis.X)
            return Block.makeCuboidShape(min, 0, 0, max, 16, 16);
        else
            return Block.makeCuboidShape(0, 0, min, 16, 16, max);
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean receiveFluid(@Nonnull IWorld world, @Nonnull BlockPos pos, BlockState state, @Nonnull IFluidState fluidState) {
        return !state.get(DOUBLE) && IWaterLoggable.super.receiveFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.get(DOUBLE) && IWaterLoggable.super.canContainFluid(world, pos, state, fluid);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (state.get(WATERLOGGED))
            world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
        return type == PathType.WATER && world.getFluidState(pos).isTagged(FluidTags.WATER);
    }

}
