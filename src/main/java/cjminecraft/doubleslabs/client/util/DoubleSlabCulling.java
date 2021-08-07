package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoubleSlabCulling {

    private static BlockState getSlabForType(BlockState state, BlockGetter world, BlockPos pos, SlabType type) {
        IHorizontalSlabSupport horizontalSupport = SlabSupport.getHorizontalSlabSupport(world, pos, state);
        if (horizontalSupport != null) {
            return horizontalSupport.getStateForHalf(Minecraft.getInstance().level, pos, state, type);
        }
        return state;
    }

    private static BlockState matchState(BlockState state1, BlockState state2, BlockGetter world, BlockPos pos1, BlockPos pos2) {
        IHorizontalSlabSupport horizontalSupport = SlabSupport.getHorizontalSlabSupport(world, pos1, state1);
        if (horizontalSupport != null) {
            IHorizontalSlabSupport otherSupport = SlabSupport.getHorizontalSlabSupport(world, pos2, state2);
            if (otherSupport != null)
                return otherSupport.getStateForHalf(Minecraft.getInstance().level, pos2, state2, horizontalSupport.getHalf(Minecraft.getInstance().level, pos1, state1));
        } else {
            IVerticalSlabSupport verticalSupport = SlabSupport.getVerticalSlabSupport(world, pos1, state1);
            if (verticalSupport != null) {
                IVerticalSlabSupport otherSupport = SlabSupport.getVerticalSlabSupport(world, pos2, state2);
                if (otherSupport != null)
                    return otherSupport.getStateForDirection(Minecraft.getInstance().level, pos2, state2, verticalSupport.getDirection(Minecraft.getInstance().level, pos1, state1));
            }
        }
        return state2;
    }

    public static boolean shouldDoubleSlabSideBeRendered(BlockState state, BlockAndTintGetter world, BlockPos pos, Direction direction) {
        BlockPos otherPos = pos.relative(direction);
        BlockState adjacentState = world.getBlockState(otherPos);

        BlockEntity tile = world.getBlockEntity(pos);

        assert tile instanceof SlabTileEntity;
        SlabTileEntity slab = (SlabTileEntity) tile;

        assert slab.getPositiveBlockInfo().getBlockState() != null;
        assert slab.getNegativeBlockInfo().getBlockState() != null;

        if (state.getBlock().equals(DSBlocks.DOUBLE_SLAB.get())) {
            // This block is a double slab

            if (adjacentState.getBlock().equals(DSBlocks.DOUBLE_SLAB.get())) {
                BlockEntity otherTile = world.getBlockEntity(otherPos);
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
            } else if (adjacentState.getBlock().equals(DSBlocks.VERTICAL_SLAB.get())) {
                Direction facing = adjacentState.getValue(VerticalSlabBlock.FACING);

                BlockEntity otherTile = world.getBlockEntity(otherPos);
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
                } else if (direction.equals(Direction.UP)) {
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

            Direction facing = state.getValue(VerticalSlabBlock.FACING);

            if (adjacentState.getBlock().equals(DSBlocks.VERTICAL_SLAB.get())) {
                // Adjacent block is a vertical slab

                BlockEntity otherTile = world.getBlockEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                Direction otherFacing = adjacentState.getValue(VerticalSlabBlock.FACING);

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
            } else if (adjacentState.getBlock().equals(DSBlocks.DOUBLE_SLAB.get())) {
                // Adjacent block is a double horizontal slab

                BlockEntity otherTile = world.getBlockEntity(otherPos);
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
                } else if (direction.equals(Direction.UP)) {
                    return shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                            otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos, direction) ||
                            shouldSideBeRendered(getSlabForType(slab.getPositiveBlockInfo().getBlockState(), world, pos, SlabType.TOP),
                                    otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos, direction);
                } else {
                    return shouldSideBeRendered(getSlabForType(slab.getNegativeBlockInfo().getBlockState(), world, pos, SlabType.BOTTOM),
                            otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos, direction);
                }
            }
        }

        IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, slab.getNegativeBlockInfo().getBlockState());
        if (horizontalSlabSupport != null) {
            BlockState doubleState = horizontalSlabSupport.getStateForHalf(Minecraft.getInstance().level, pos, slab.getPositiveBlockInfo().getBlockState(), SlabType.DOUBLE);
            return Block.shouldRenderFace(doubleState, world, pos, direction, otherPos);
        }

        return Block.shouldRenderFace(state, world, pos, direction, otherPos);
    }

    public static boolean shouldSideBeRendered(BlockState state, BlockAndTintGetter world, BlockPos pos, Direction direction, boolean positive) {
        BlockPos otherPos = pos.relative(direction);
        BlockState adjacentState = world.getBlockState(otherPos);

        BlockEntity tile = world.getBlockEntity(pos);

        assert tile instanceof SlabTileEntity;
        SlabTileEntity slab = (SlabTileEntity) tile;

        if (state.getBlock().equals(DSBlocks.DOUBLE_SLAB.get())) {
            // This block is a double slab

            IBlockInfo currentBlock = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

            assert currentBlock.getBlockState() != null;

            if (adjacentState.getBlock().equals(DSBlocks.DOUBLE_SLAB.get())) {
                BlockEntity otherTile = world.getBlockEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                IBlockInfo otherBlock;
                if (direction.getAxis().isHorizontal())
                    otherBlock = positive ? otherSlab.getPositiveBlockInfo() : otherSlab.getNegativeBlockInfo();
                else if (direction.equals(Direction.UP))
                    otherBlock = otherSlab.getNegativeBlockInfo();
                else
                    otherBlock = otherSlab.getPositiveBlockInfo();

                assert otherBlock.getBlockState() != null;

                return shouldSideBeRendered(currentBlock.getBlockState(), otherBlock.getBlockState(),
                        world, pos, otherPos, direction);
            } else if (adjacentState.getBlock().equals(DSBlocks.VERTICAL_SLAB.get())) {
                Direction facing = adjacentState.getValue(VerticalSlabBlock.FACING);

                BlockEntity otherTile = world.getBlockEntity(otherPos);
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
                } else if (direction.equals(Direction.UP)) {
                    assert slab.getPositiveBlockInfo().getBlockState() != null;
                    return otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                            otherSlab.getPositiveBlockInfo().getBlockState() == null ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    getSlabForType(otherSlab.getNegativeBlockInfo().getBlockState(), world, otherPos, SlabType.BOTTOM),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getPositiveBlockInfo().getBlockState(),
                                    getSlabForType(otherSlab.getPositiveBlockInfo().getBlockState(), world, otherPos, SlabType.BOTTOM),
                                    world, pos, otherPos, direction);
                } else {
                    assert slab.getNegativeBlockInfo().getBlockState() != null;
                    return otherSlab.getNegativeBlockInfo().getBlockState() == null ||
                            otherSlab.getPositiveBlockInfo().getBlockState() == null ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    getSlabForType(otherSlab.getNegativeBlockInfo().getBlockState(), world, otherPos, SlabType.TOP),
                                    world, pos, otherPos, direction) ||
                            shouldSideBeRendered(slab.getNegativeBlockInfo().getBlockState(),
                                    getSlabForType(otherSlab.getPositiveBlockInfo().getBlockState(), world, otherPos, SlabType.TOP),
                                    world, pos, otherPos, direction);
                }
            }
        } else {
            // This block is a vertical slab

            Direction facing = state.getValue(VerticalSlabBlock.FACING);

            if (adjacentState.getBlock().equals(DSBlocks.VERTICAL_SLAB.get())) {
                // Adjacent block is a vertical slab

                BlockEntity otherTile = world.getBlockEntity(otherPos);
                assert otherTile instanceof SlabTileEntity;
                SlabTileEntity otherSlab = (SlabTileEntity) otherTile;

                Direction otherFacing = adjacentState.getValue(VerticalSlabBlock.FACING);

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
                        IBlockInfo block = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

                        return otherSlab.getPositiveBlockInfo().getBlockState() == null
                                || otherSlab.getNegativeBlockInfo().getBlockState() == null
                                || block.getBlockState() == null
                                || shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherSlab.getPositiveBlockInfo().getBlockState(), world, pos, otherPos),
                                        world, pos, otherPos, direction)
                                || shouldSideBeRendered(block.getBlockState(),
                                        matchState(block.getBlockState(), otherSlab.getNegativeBlockInfo().getBlockState(), world, pos, otherPos),
                                        world, pos, otherPos, direction);
                    }
                }
            } else if (adjacentState.getBlock().equals(DSBlocks.DOUBLE_SLAB.get())) {
                // Adjacent block is a double horizontal slab

                BlockEntity otherTile = world.getBlockEntity(otherPos);
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
                } else if (direction.equals(Direction.UP)) {
                    BlockState otherState = otherSlab.getNegativeBlockInfo().getBlockState();
                    return block.getBlockState() == null ||
                            shouldSideBeRendered(getSlabForType(block.getBlockState(), world, pos, SlabType.TOP),
                            otherState, world, pos, otherPos, direction);
                } else {
                    BlockState otherState = otherSlab.getPositiveBlockInfo().getBlockState();
                    return block.getBlockState() == null ||
                            shouldSideBeRendered(getSlabForType(block.getBlockState(), world, pos, SlabType.BOTTOM),
                            otherState, world, pos, otherPos, direction);
                }
            }
        }

        IBlockInfo block = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

        if (block.getBlockState() != null) {
            IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, block.getBlockState());
            if (horizontalSlabSupport != null) {
                BlockState doubleState = horizontalSlabSupport.getStateForHalf(Minecraft.getInstance().level, pos, block.getBlockState(), SlabType.DOUBLE);
                return Block.shouldRenderFace(doubleState, world, pos, direction, otherPos);
            }
        }

        return Block.shouldRenderFace(state, world, pos, direction, otherPos);
    }

    public static boolean shouldSideBeRendered(BlockState state, BlockState otherState, BlockGetter world, BlockPos pos, BlockPos otherPos, Direction direction) {
        assert state != null;
        assert otherState != null;

        if (state.skipRendering(otherState, direction)) {
            return false;
        } else if (otherState.canOcclude()) {
            Block.BlockStatePairKey block$rendersidecachekey = new Block.BlockStatePairKey(state, otherState, direction);
            Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap = Block.OCCLUSION_CACHE.get();
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
            if (b0 != 127) {
                return b0 != 0;
            } else {
                VoxelShape voxelshape = state.getFaceOcclusionShape(world, pos, direction);
                VoxelShape voxelshape1 = otherState.getFaceOcclusionShape(world, otherPos, direction.getOpposite());
                boolean flag = Shapes.joinIsNotEmpty(voxelshape, voxelshape1, BooleanOp.ONLY_FIRST);
                if (object2bytelinkedopenhashmap.size() == 2048) {
                    object2bytelinkedopenhashmap.removeLastByte();
                }

                object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte) (flag ? 1 : 0));
                return flag;
            }
        } else {
            return true;
        }
    }

}
