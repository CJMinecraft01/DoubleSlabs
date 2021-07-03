package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.function.BiFunction;

public class DoubleSlabCulling {

    private static IBlockState getSlabForType(IBlockState state, IBlockAccess world, BlockPos pos, BlockSlab.EnumBlockHalf type) {
        IHorizontalSlabSupport horizontalSupport = SlabSupport.getHorizontalSlabSupport(world, pos, state);
        if (horizontalSupport != null) {
            return horizontalSupport.getStateForHalf(Minecraft.getMinecraft().world, pos, state, type);
        }
        return state;
    }

    private static IBlockState matchState(IBlockState state1, IBlockState state2, IBlockAccess world, BlockPos pos1, BlockPos pos2) {
        IHorizontalSlabSupport horizontalSupport = SlabSupport.getHorizontalSlabSupport(world, pos1, state1);
        if (horizontalSupport != null) {
            IHorizontalSlabSupport otherSupport = SlabSupport.getHorizontalSlabSupport(world, pos2, state2);
            if (otherSupport != null)
                return otherSupport.getStateForHalf(Minecraft.getMinecraft().world, pos2, state2, horizontalSupport.getHalf(Minecraft.getMinecraft().world, pos1, state1));
        } else {
            IVerticalSlabSupport verticalSupport = SlabSupport.getVerticalSlabSupport(world, pos1, state1);
            if (verticalSupport != null) {
                IVerticalSlabSupport otherSupport = SlabSupport.getVerticalSlabSupport(world, pos2, state2);
                if (otherSupport != null)
                    return otherSupport.getStateForDirection(Minecraft.getMinecraft().world, pos2, state2, verticalSupport.getDirection(Minecraft.getMinecraft().world, pos1, state1));
            }
        }
        return state2;
    }

    public static boolean shouldDoubleSlabSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction) {
        BlockPos otherPos = pos.offset(direction);
        IBlockState adjacentState = world.getBlockState(otherPos);

        TileEntity tile = world.getTileEntity(pos);

        assert tile instanceof SlabTileEntity;
        SlabTileEntity slab = (SlabTileEntity) tile;

        assert slab.getPositiveBlockInfo().getBlockState() != null;
        assert slab.getNegativeBlockInfo().getBlockState() != null;

        if (state.getBlock().equals(DSBlocks.DOUBLE_SLAB)) {
            // This block is a double slab

            if (adjacentState.getBlock().equals(DSBlocks.DOUBLE_SLAB)) {
                TileEntity otherTile = world.getTileEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                assert otherSlab.getPositiveBlockInfo().getBlockState() != null;
                assert otherSlab.getNegativeBlockInfo().getBlockState() != null;

                return shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                        otherSlab.getPositiveBlockInfo().getBlockState(),
                        world, pos, otherPos, direction) ||
                        shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                otherSlab.getNegativeBlockInfo().getBlockState(),
                                world, pos, otherPos, direction);
            } else if (adjacentState.getBlock().equals(DSBlocks.VERTICAL_SLAB)) {
                EnumFacing facing = adjacentState.getValue(VerticalSlabBlock.FACING);

                TileEntity otherTile = world.getTileEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                if (direction.getAxis().equals(facing.getAxis())) {
                    IBlockInfo block = direction.equals(facing) ? otherSlab.getNegativeBlockInfo() : otherSlab.getPositiveBlockInfo();

                    return block.getBlockState() == null ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), block.getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    matchState(slab.getPositiveBlockInfo().getBlockState(), block.getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else if (direction.getAxis().isHorizontal()) {
                    return otherSlab.getPositiveBlockInfo().getBlockState() == null ||
                            otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    matchState(slab.getPositiveBlockInfo().getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    matchState(slab.getPositiveBlockInfo().getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else if (direction.equals(EnumFacing.UP)) {
                    return otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                            otherSlab.getPositiveBlockInfo().getBlockState() == null ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else {
                    return otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                            otherSlab.getPositiveBlockInfo().getBlockState() == null ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    matchState(slab.getPositiveBlockInfo().getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    matchState(slab.getPositiveBlockInfo().getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                }
            }
        } else {
            // This block is a vertical slab

            EnumFacing facing = state.getValue(VerticalSlabBlock.FACING);

            if (adjacentState.getBlock().equals(DSBlocks.VERTICAL_SLAB)) {
                // Adjacent block is a vertical slab

                TileEntity otherTile = world.getTileEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                EnumFacing otherFacing = adjacentState.getValue(VerticalSlabBlock.FACING);

                if (otherFacing.getAxis().equals(facing.getAxis())) {
                    if (direction.getAxis().equals(facing.getAxis())) {
                        IBlockInfo block = direction.equals(facing) ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();
                        IBlockInfo otherBlock = direction.equals(otherFacing) ? otherSlab.getNegativeBlockInfo() : otherSlab.getPositiveBlockInfo();

                        return otherBlock.getBlockState() == null ||
                                shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherBlock.getBlockState(), world, pos, otherPos),
                                        world, pos, otherPos, direction);
                    } else {
                        IBlockInfo positiveBlock = otherFacing.equals(facing) ? otherSlab.getPositiveBlockInfo() : otherSlab.getNegativeBlockInfo();
                        IBlockInfo negativeBlock = otherFacing.equals(facing) ? otherSlab.getNegativeBlockInfo() : otherSlab.getPositiveBlockInfo();

                        return otherSlab.getPositiveBlockInfo().getBlockState() == null || otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                                (shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                        matchState(slab.getPositiveBlockInfo().getBlockState(), positiveBlock.getBlockState(), world, pos, otherPos), world, pos, otherPos, direction)
                                        || shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                        matchState(slab.getNegativeBlockInfo().getBlockState(), negativeBlock.getBlockState(), world, pos, otherPos), world, pos, otherPos, direction));
                    }
                } else {
                    if (direction.getAxis().equals(facing.getAxis())) {
                        IBlockInfo block = direction.equals(facing) ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

                        return otherSlab.getPositiveBlockInfo().getBlockState() == null || otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                                (shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos), world, pos, otherPos, direction)
                                        || shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos), world, pos, otherPos, direction));
                    } else {
                        IBlockInfo otherBlock = direction.equals(otherFacing) ? otherSlab.getNegativeBlockInfo() : otherSlab.getPositiveBlockInfo();

                        return otherBlock.getBlockState() == null ||
                                (shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                        matchState(slab.getPositiveBlockInfo().getBlockState(), otherBlock.getBlockState(), world, pos, otherPos),
                                        world, pos, otherPos, direction)
                                        || shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                        matchState(slab.getNegativeBlockInfo().getBlockState(), otherBlock.getBlockState(), world, pos, otherPos),
                                        world, pos, otherPos, direction));
                    }
                }
            } else if (adjacentState.getBlock().equals(DSBlocks.DOUBLE_SLAB)) {
                // Adjacent block is a double horizontal slab

                TileEntity otherTile = world.getTileEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                if (direction.getAxis().equals(facing.getAxis())) {
                    IBlockInfo block = direction.equals(facing) ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

                    return shouldSideBeRendered(block.getBlockState(),
                            matchState(block.getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                            world, pos, otherPos, direction) ||
                            shouldSideBeRendered(block.getBlockState(),
                                    matchState(block.getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else if (direction.getAxis().isHorizontal()) {
                    return shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                            matchState(slab.getPositiveBlockInfo().getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                            world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    matchState(slab.getPositiveBlockInfo().getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else if (direction.equals(EnumFacing.UP)) {
                    return shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                            otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos, direction) ||
                            shouldSideBeRendered(getSlabForType(slab.getPositiveBlockInfo().getBlockState(), world, pos, BlockSlab.EnumBlockHalf.TOP),
                                    otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos, direction);
                } else {
                    return shouldSideBeRendered(getSlabForType(slab.getNegativeBlockInfo().getBlockState(), world, pos, BlockSlab.EnumBlockHalf.BOTTOM),
                            otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos, direction);
                }
            }
        }

        IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, slab.getNegativeBlockInfo().getBlockState());
        if (horizontalSlabSupport != null) {
            IBlockState doubleState = horizontalSlabSupport.getStateForHalf(Minecraft.getMinecraft().world, pos, slab.getPositiveBlockInfo().getBlockState(), null);
            return doubleState.shouldSideBeRendered(world, pos, direction);
        }

        return state.shouldSideBeRendered(world, pos, direction);
    }

    public static boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, boolean positive) {
        BlockPos otherPos = pos.offset(direction);
        IBlockState adjacentState = world.getBlockState(otherPos);

        TileEntity tile = world.getTileEntity(pos);

        assert tile instanceof SlabTileEntity;
        SlabTileEntity slab = (SlabTileEntity) tile;

        if (state.getBlock().equals(DSBlocks.DOUBLE_SLAB)) {
            // This block is a double slab

            IBlockInfo currentBlock = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

            assert currentBlock.getBlockState() != null;

            if (adjacentState.getBlock().equals(DSBlocks.DOUBLE_SLAB)) {
                TileEntity otherTile = world.getTileEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                IBlockInfo otherBlock;
                if (direction.getAxis().isHorizontal())
                    otherBlock = positive ? otherSlab.getPositiveBlockInfo() : otherSlab.getNegativeBlockInfo();
                else if (direction.equals(EnumFacing.UP))
                    otherBlock = otherSlab.getNegativeBlockInfo();
                else
                    otherBlock = otherSlab.getPositiveBlockInfo();

                assert otherBlock.getBlockState() != null;

                return shouldSideBeRendered(currentBlock.getBlockState(), otherBlock.getBlockState(),
                        world, pos, otherPos, direction);
            } else if (adjacentState.getBlock().equals(DSBlocks.VERTICAL_SLAB)) {
                EnumFacing facing = adjacentState.getValue(VerticalSlabBlock.FACING);

                TileEntity otherTile = world.getTileEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                if (direction.getAxis().equals(facing.getAxis())) {
                    IBlockInfo block = direction.equals(facing) ? otherSlab.getNegativeBlockInfo() : otherSlab.getPositiveBlockInfo();

                    return block.getBlockState() == null ||
                            shouldSideBeRendered(currentBlock.getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), block.getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else if (direction.getAxis().isHorizontal()) {
                    return otherSlab.getPositiveBlockInfo().getBlockState() == null ||
                            otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                            shouldSideBeRendered(currentBlock.getBlockState(),
                                    matchState(slab.getNegativeBlockInfo().getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(currentBlock.getBlockState(),
                                    matchState(slab.getPositiveBlockInfo().getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else if (direction.equals(EnumFacing.UP)) {
                    assert slab.getPositiveBlockInfo().getBlockState() != null;
                    return otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                            otherSlab.getPositiveBlockInfo().getBlockState() == null ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    getSlabForType(otherSlab.getNegativeBlockInfo().getBlockState(), world, otherPos, BlockSlab.EnumBlockHalf.BOTTOM),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    getSlabForType(otherSlab.getPositiveBlockInfo().getBlockState(), world, otherPos, BlockSlab.EnumBlockHalf.BOTTOM),
                                    world, pos, otherPos, direction);
                } else {
                    assert slab.getNegativeBlockInfo().getBlockState() != null;
                    return otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                            otherSlab.getPositiveBlockInfo().getBlockState() == null ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    getSlabForType(otherSlab.getNegativeBlockInfo().getBlockState(), world, otherPos, BlockSlab.EnumBlockHalf.TOP),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    getSlabForType(otherSlab.getPositiveBlockInfo().getBlockState(), world, otherPos, BlockSlab.EnumBlockHalf.TOP),
                                    world, pos, otherPos, direction);
                }
            }
        } else {
            // This block is a vertical slab

            EnumFacing facing = state.getValue(VerticalSlabBlock.FACING);

            if (adjacentState.getBlock().equals(DSBlocks.VERTICAL_SLAB)) {
                // Adjacent block is a vertical slab

                TileEntity otherTile = world.getTileEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                EnumFacing otherFacing = adjacentState.getValue(VerticalSlabBlock.FACING);

                if (otherFacing.getAxis().equals(facing.getAxis())) {
                    if (direction.getAxis().equals(facing.getAxis())) {
                        IBlockInfo block = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();
                        IBlockInfo otherBlock = direction.equals(otherFacing) ? otherSlab.getNegativeBlockInfo() : otherSlab.getPositiveBlockInfo();

                        return otherBlock.getBlockState() == null || block.getBlockState() == null ||
                                shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherBlock.getBlockState(), world, pos, otherPos),
                                        world, pos, otherPos, direction);
                    } else {
                        IBlockInfo positiveBlock = otherFacing.equals(facing) ? otherSlab.getPositiveBlockInfo() : otherSlab.getNegativeBlockInfo();
                        IBlockInfo negativeBlock = otherFacing.equals(facing) ? otherSlab.getNegativeBlockInfo() : otherSlab.getPositiveBlockInfo();
                        IBlockInfo otherBlock = positive ? positiveBlock : negativeBlock;

                        IBlockInfo currentBlock = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

                        return otherBlock.getBlockState() == null || currentBlock.getBlockState() == null ||
                                shouldSideBeRendered(currentBlock.getBlockState(),
                                        matchState(currentBlock.getBlockState(), otherBlock.getBlockState(), world, pos, otherPos), world, pos, otherPos, direction);
                    }
                } else {
                    if (direction.getAxis().equals(facing.getAxis())) {
                        IBlockInfo block = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

                        return otherSlab.getPositiveBlockInfo().getBlockState() == null || otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                                block.getBlockState() == null ||
                                (shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos), world, pos, otherPos, direction)
                                        || shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos), world, pos, otherPos, direction));
                    } else {
                        IBlockInfo otherBlock = direction.equals(otherFacing) ? otherSlab.getNegativeBlockInfo() : otherSlab.getPositiveBlockInfo();

                        IBlockInfo block = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

                        return otherBlock.getBlockState() == null || block.getBlockState() == null ||
                                shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherBlock.getBlockState(), world, pos, otherPos),
                                        world, pos, otherPos, direction);
                    }
                }
            } else if (adjacentState.getBlock().equals(DSBlocks.DOUBLE_SLAB)) {
                // Adjacent block is a double horizontal slab

                TileEntity otherTile = world.getTileEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                IBlockInfo block = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

                if (direction.getAxis().equals(facing.getAxis())) {
                    return block.getBlockState() == null ||
                            shouldSideBeRendered(block.getBlockState(),
                                    matchState(block.getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(block.getBlockState(),
                                    matchState(block.getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else if (direction.getAxis().isHorizontal()) {
                    return block.getBlockState() == null ||
                            shouldSideBeRendered(block.getBlockState(),
                                    matchState(block.getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(block.getBlockState(),
                                    matchState(block.getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                    world, pos, otherPos, direction);
                } else if (direction.equals(EnumFacing.UP)) {
                    IBlockState otherState = otherSlab.getNegativeBlockInfo().getBlockState();
                    return block.getBlockState() == null ||
                            shouldSideBeRendered(getSlabForType(block.getBlockState(), world, pos, BlockSlab.EnumBlockHalf.TOP),
                                    otherState, world, pos, otherPos, direction);
                } else {
                    IBlockState otherState = otherSlab.getPositiveBlockInfo().getBlockState();
                    return block.getBlockState() == null ||
                            shouldSideBeRendered(getSlabForType(block.getBlockState(), world, pos, BlockSlab.EnumBlockHalf.BOTTOM),
                                    otherState, world, pos, otherPos, direction);
                }
            }
        }

        IBlockInfo block = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

        if (block.getBlockState() != null) {
            IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, block.getBlockState());
            if (horizontalSlabSupport != null) {
                IBlockState doubleState = horizontalSlabSupport.getStateForHalf(Minecraft.getMinecraft().world, pos, block.getBlockState(), null);
                return doubleState.shouldSideBeRendered(world, pos, direction);
            }
        }

        return state.shouldSideBeRendered(world, pos, direction);
    }

    private static boolean isEmpty(AxisAlignedBB shape) {
        return shape.minX >= shape.maxX && shape.minY >= shape.maxY && shape.minZ >= shape.maxZ;
    }

    public static boolean compare(AxisAlignedBB shape1, AxisAlignedBB shape2, BiFunction<Boolean, Boolean, Boolean> function) {
        if (function.apply(false, false)) {
            throw new IllegalArgumentException();
        } else if (shape1 == shape2) {
            return function.apply(true, true);
        } else if (isEmpty(shape1)) {
            return function.apply(false, !isEmpty(shape2));
        } else if (isEmpty(shape2)) {
            return function.apply(!isEmpty(shape1), false);
        } else {
            boolean flag = function.apply(true, false);
            boolean flag1 = function.apply(false, true);

            if (shape1.maxX < shape2.minX - 1.0E-7D)
                return flag || flag1;
            if (shape2.maxX < shape1.minX - 1.0E-7D)
                return flag || flag1;
            if (shape1.maxY < shape2.minY - 1.0E-7D)
                return flag || flag1;
            if (shape2.maxY < shape1.minY - 1.0E-7D)
                return flag || flag1;
            if (shape1.maxZ < shape2.minZ - 1.0E-7D)
                return flag || flag1;
            if (shape2.maxZ < shape1.minZ - 1.0E-7D)
                return flag || flag1;

            return shape1.intersects(shape2);
        }
    }

    public static boolean shouldSideBeRendered(IBlockState state, IBlockState otherState, IBlockAccess world, BlockPos pos, BlockPos otherPos, EnumFacing side) {
        assert state != null;
        assert otherState != null;

        final IBlockAccess wrappedWorld = new BlockAccessWrapper(world, pos, otherPos, state, otherState);

//        if (!state.shouldSideBeRendered(wrappedWorld, pos, side)) {
//            return false;
//        } else
        if (!ClientConstants.isTransparent(state)) {
            AxisAlignedBB bounding1 = otherState.getBoundingBox(world, otherPos);
            AxisAlignedBB bounding2 = state.getBoundingBox(world, pos);
            // cache?
            return compare(bounding1, bounding2, (a, b) -> a && !b);
        } else {
            return true;
        }
    }

}
