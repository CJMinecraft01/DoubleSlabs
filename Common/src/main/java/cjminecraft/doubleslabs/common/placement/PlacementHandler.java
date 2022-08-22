package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.block.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import cjminecraft.doubleslabs.common.util.DoubleSlabPlaceContext;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Consumer;

public class PlacementHandler {

    private static boolean activateBlock(Level level, BlockPos pos, Player player, InteractionHand hand) {
        boolean useItem = !player.getMainHandItem().doesSneakBypassUse(level, pos, player) || !player.getOffhandItem().doesSneakBypassUse(level, pos, player);
        boolean flag = player.isSecondaryUseActive() && useItem;
        if (!flag) {
            InteractionResult result = level.getBlockState(pos).use(level, player, hand, RayTraceUtil.rayTrace(player).withPosition(pos));
            return result.consumesAction();
        }
        return false;
    }

    private static BlockState prepareState(BlockState state) {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED))
            return state.setValue(BlockStateProperties.WATERLOGGED, false);
        return state;
    }

    public static BlockPlaceContext getUseContext(Player player, InteractionHand hand, ItemStack stack) {
        return new BlockPlaceContext(player, hand, stack, RayTraceUtil.rayTrace(player));
    }

    public static BlockPlaceContext getUseContext(Player player, InteractionHand hand, ItemStack stack, BlockPos pos) {
        return new DoubleSlabPlaceContext(player, hand, stack, RayTraceUtil.rayTrace(player).withPosition(pos));
    }

    public static BlockState getStateFromSupport(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, SlabType half, IHorizontalSlabSupport support) {
        return support.getStateForHalf(level, pos, support.getStateFromStack(stack, getUseContext(player, hand, stack, pos)), half);
    }

    public static BlockState getStateFromSupport(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, Direction direction, IVerticalSlabSupport support) {
        return support.getStateForDirection(level, pos, support.getStateFromStack(stack, getUseContext(player, hand, stack, pos)), direction);
    }

    private static InteractionResult finishBlockPlacement(Level level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
        // todo: change to sensitive version
//        SoundType soundType = state.getSoundType(level, pos, player);
        SoundType soundType = state.getSoundType();
        level.playSound(player, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
        if (!player.isCreative())
            stack.shrink(1);
        if (player instanceof ServerPlayer)
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, pos, stack);
        return InteractionResult.SUCCESS;
    }

    private static boolean placeSlab(Level level, BlockPos pos, BlockState state, BlockPlaceContext context, Consumer<SlabBlockEntity> setStates) {
        if (!context.canPlace())
            return false;
        if (level.setBlock(pos, state, 11)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SlabBlockEntity<?> slab) {
                setStates.accept(slab);
            }
            return true;
        }
        return false;
    }

    private static boolean placeSlab(Level level, BlockPos pos, BlockState state, BlockPlaceContext context, BlockState negativeState, BlockState positiveState) {
        return placeSlab(level, pos, state, context, slab -> {
            slab.getNegativeBlockInfo().setBlockState(negativeState);
            slab.getPositiveBlockInfo().setBlockState(positiveState);
        });
    }

    private static boolean shouldPlaceVerticalSlab(Player player, Direction face) {
        if (DSConfig.COMMON.disableVerticalSlabPlacement.get())
            return false;
        IPlayerConfig config = player.getCapability(PlayerConfigCapability.PLAYER_CONFIG).orElse(new PlayerConfig());

        return config.getVerticalSlabPlacementMethod().shouldPlace(player, face, config.placeVerticalSlabs());
    }
    
    public static InteractionResult onItemUse(Level level, Player player, Direction face, BlockPos pos, ItemStack originalStack, InteractionHand hand) {
        if (!originalStack.isEmpty()) {
            ItemStack stack = originalStack;
            if (stack.getItem() == Services.REGISTRIES.getItems().getVerticalSlabItem())
                stack = VerticalSlabItem.getStack(stack);
            IHorizontalSlabSupport horizontalSlabItemSupport = SlabSupport.getHorizontalSlabSupport(stack, player, hand);

            BlockState state = level.getBlockState(pos);

            BlockPlaceContext context = getUseContext(player, hand, stack, pos);

            if (horizontalSlabItemSupport == null) {
                // The item we are holding is not a horizontal slab

                // Check if the item is a supported vertical slab
                IVerticalSlabSupport verticalSlabItemSupport = SlabSupport.getVerticalSlabSupport(stack, player, hand);

                // If not then don't do anything special
                if (verticalSlabItemSupport == null)
                    return InteractionResult.PASS;

                boolean offset = false;

                if (state.getBlock() != Services.REGISTRIES.getBlocks().getVerticalSlabBlock() && level.getBlockState(pos.relative(face)).getBlock() == Services.REGISTRIES.getBlocks().getVerticalSlabBlock()) {
                    pos = pos.relative(face);
                    state = level.getBlockState(pos);
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    offset = true;
                    if (blockEntity instanceof SlabBlockEntity<?>)
                        face = ((SlabBlockEntity<?>) blockEntity).getPositiveBlockInfo().getBlockState() != null ? state.getValue(VerticalSlabBlock.FACING).getOpposite() : state.getValue(VerticalSlabBlock.FACING);
                }

                // Check if the block that they clicked on is a vertical slab
                if (state.getBlock() == Services.REGISTRIES.getBlocks().getVerticalSlabBlock()) {
                    // If we are trying to mix to one of our vertical slabs
                    if (!state.getValue(VerticalSlabBlock.DOUBLE) && face == state.getValue(VerticalSlabBlock.FACING).getOpposite()) {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        // Check that the tile has been created and that the shift key isn't pressed and that we are clicking on the face that is inside of the block
                        if (blockEntity instanceof SlabBlockEntity<?> && !player.isCrouching() && (face != state.getValue(VerticalSlabBlock.FACING) || ((SlabBlockEntity<?>) blockEntity).getPositiveBlockInfo().getBlockState() == null)) {
                            // The new state for the vertical slab with the double property set
                            BlockState newState = state.setValue(VerticalSlabBlock.DOUBLE, true);
                            // Get the correct slab state for the vertical slab
                            BlockState slabState = getStateFromSupport(level, pos, player, hand, stack, ((SlabBlockEntity<?>) blockEntity).getPositiveBlockInfo().getBlockState() != null ? face.getOpposite() : face, verticalSlabItemSupport);
                            if (DSConfig.COMMON.isBlacklistedVerticalSlab(slabState.getBlock()))
                                return InteractionResult.PASS;

                            if (!offset && activateBlock(level, pos, player, hand))
                                return InteractionResult.PASS;

                            if (placeSlab(level, pos, newState, context, slab -> {
                                if (slab.getPositiveBlockInfo().getBlockState() != null)
                                    slab.getNegativeBlockInfo().setBlockState(slabState);
                                else
                                    slab.getPositiveBlockInfo().setBlockState(slabState);
                            })) {
                                return finishBlockPlacement(level, pos, slabState, player, originalStack);
                            }
                        }
                    }
                } else {
                    // Otherwise check if we are trying to mix two vertical slabs from different mods

                    // Check that the block is a vertical slab
                    IVerticalSlabSupport blockSupport = SlabSupport.getVerticalSlabSupport(level, pos, state);

                    // If not, try offsetting by the face
                    if (blockSupport == null) {
                        offset = true;
                        // Offset the position
                        pos = pos.relative(face);
                        // todo Check the player isn't standing there
                        state = level.getBlockState(pos);

                        blockSupport = SlabSupport.getVerticalSlabSupport(level, pos, state);
                        if (blockSupport == null)
                            return InteractionResult.PASS;

                        face = blockSupport.getDirection(level, pos, state).getOpposite();
                    }

                    if (DSConfig.COMMON.isBlacklistedVerticalSlab(state.getBlock()))
                        return InteractionResult.PASS;

                    if (!offset && activateBlock(level, pos, player, hand))
                        return InteractionResult.PASS;

                    state = prepareState(state);

                    // Get the direction that the vertical slab block is facing
                    Direction direction = blockSupport.getDirection(level, pos, state);

                    if (face == direction.getOpposite()) {
                        // Get the state for the vertical slab item using the direction of the already placed vertical slab
                        BlockState slabState = getStateFromSupport(level, pos, player, hand, stack, direction.getOpposite(), verticalSlabItemSupport);
                        if (DSConfig.COMMON.isBlacklistedVerticalSlab(slabState.getBlock()))
                            return InteractionResult.PASS;
                        // Create the state for the vertical slab
                        BlockState newState = Services.REGISTRIES.getBlocks().getVerticalSlabBlock().defaultBlockState().setValue(VerticalSlabBlock.FACING, direction).setValue(VerticalSlabBlock.DOUBLE, true);

                        // Try to set the block state
                        BlockState finalState = state;
                        if (placeSlab(level, pos, newState, context, slab -> {
                            slab.getPositiveBlockInfo().setBlockState(slabState);
                            slab.getNegativeBlockInfo().setBlockState(finalState);
                        }))
                            return finishBlockPlacement(level, pos, slabState, player, originalStack);
                    }
                }
            } else {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(level, pos, state);

                boolean verticalSlab = state.getBlock() == Services.REGISTRIES.getBlocks().getVerticalSlabBlock() && !state.getValue(VerticalSlabBlock.DOUBLE) && (((SlabBlockEntity<?>) level.getBlockEntity(pos)).getPositiveBlockInfo().getBlockState() != null ? face == state.getValue(VerticalSlabBlock.FACING).getOpposite() : face == state.getValue(VerticalSlabBlock.FACING));

                boolean offset = false;

                if (horizontalSlabSupport == null && !verticalSlab) {
                    // The block at the position which was clicked is not a horizontal slab

                    // Check if the block is instead a vertical slab block
                    IVerticalSlabSupport verticalSlabSupport = SlabSupport.getVerticalSlabSupport(level, pos, state);
                    if (verticalSlabSupport != null) {
                        // If so try and combine a regular horizontal slab with a vertical slab

                        if (DSConfig.COMMON.isBlacklistedVerticalSlab(state.getBlock()))
                            return InteractionResult.PASS;

                        // Get the direction of the vertical slab
                        Direction direction = verticalSlabSupport.getDirection(level, pos, state);

                        // If we are placing on the front of the vertical slab
                        if (face == direction) {
                            state = prepareState(state);

                            if (activateBlock(level, pos, player, hand, cancel))
                                return InteractionResult.PASS;

                            BlockState slabState = getStateFromSupport(level, pos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                            BlockState newState = Services.REGISTRIES.getBlocks().getVerticalSlabBlock().getStateForPlacement(context).setValue(VerticalSlabBlock.DOUBLE, true).setValue(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(level, pos, newState, context, state, slabState))
                                return finishBlockPlacement(level, pos, slabState, player, originalStack);
                            return InteractionResult.PASS;
                        }
                    }

//                    if (activateBlock(level, pos, player, hand, cancel))
//                        return;

                    BlockPos newPos = pos.relative(face);
                    BlockState newState = level.getBlockState(newPos);

                    offset = true;

//                    if (!canPlace(level, newPos, face, player, hand, stack, cancel, false))
//                        return;

                    verticalSlab = newState.getBlock() == Services.REGISTRIES.getBlocks().getVerticalSlabBlock() && !newState.getValue(VerticalSlabBlock.DOUBLE);

                    horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(level, newPos, newState);
                    if (horizontalSlabSupport == null && !verticalSlab) {
                        // If the offset block is not a horizontal slab and is not a dynamic vertical slab
                        verticalSlabSupport = SlabSupport.getVerticalSlabSupport(level, newPos, newState);
                        if (verticalSlabSupport != null) {
                            // The offset block is a vertical slab from another mod so we should try to combine them with a regular slab

                            if (DSConfig.COMMON.isBlacklistedVerticalSlab(newState.getBlock()))
                                return InteractionResult.PASS;

                            newState = prepareState(newState);

                            Direction direction = verticalSlabSupport.getDirection(level, newPos, newState);

                            BlockState slabState = getStateFromSupport(level, newPos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                            BlockState verticalSlabState = Services.REGISTRIES.getBlocks().getVerticalSlabBlock().getStateForPlacement(context).setValue(VerticalSlabBlock.DOUBLE, true).setValue(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(level, newPos, verticalSlabState, context, newState, slabState))
                                return finishBlockPlacement(level, pos, slabState, player, originalStack);
                        } else if (shouldPlaceVerticalSlab(player, face)) {
                            // We should place the horizontal slab as a vertical slab
                            if (state.canBeReplaced(context)) {
                                newState = state;
                                newPos = pos;
                                if (activateBlock(level, pos, player, hand))
                                    return InteractionResult.PASS;
                            } else if (!newState.canBeReplaced(context))
                                return InteractionResult.PASS;

                            BlockState slabState = getStateFromSupport(level, newPos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                            if (DSConfig.COMMON.isBlacklistedVerticalSlab(slabState.getBlock()))
                                return InteractionResult.PASS;

                            if (activateBlock(level, newPos, player, hand))
                                return InteractionResult.PASS;

                            context = getUseContext(player, hand, stack, newPos);

                            BlockState verticalSlabState = Services.REGISTRIES.getBlocks().getVerticalSlabBlock().getStateForPlacement(context);

                            if (placeSlab(level, newPos, verticalSlabState, context, slab -> slab.getPositiveBlockInfo().setBlockState(slabState)))
                                return finishBlockPlacement(level, newPos, slabState, player, originalStack);
                        }
                        return InteractionResult.PASS;
                    }
                    state = newState;
                    pos = newPos;
                    if (horizontalSlabSupport != null)
                        face = horizontalSlabSupport.getHalf(level, pos, newState) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN;
                }

                // Check if the block is a dynamic vertical slab and try to join the two slabs together
                if (verticalSlab) {
                    if (!offset && activateBlock(level, pos, player, hand))
                        return InteractionResult.PASS;
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof SlabBlockEntity<?> slab && !player.isCrouching() && (face != state.getValue(VerticalSlabBlock.FACING) || ((SlabBlockEntity<?>) blockEntity).getPositiveBlockInfo().getBlockState() == null)) {
                        FluidState fluidstate = level.getFluidState(pos);
                        BlockState newState = state.setValue(VerticalSlabBlock.DOUBLE, true).setValue(VerticalSlabBlock.WATERLOGGED, fluidstate.getType() == Fluids.WATER && VerticalSlabBlock.either(level, pos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getLevel(), i.getPos(), i.getBlockState())));
                        BlockState slabState = getStateFromSupport(level, pos, player, hand, stack, slab.getPositiveBlockInfo().getBlockState() != null ? SlabType.TOP : SlabType.BOTTOM, horizontalSlabItemSupport);
                        if (DSConfig.COMMON.isBlacklistedVerticalSlab(slabState.getBlock()))
                            return InteractionResult.PASS;

                        if (placeSlab(level, pos, newState, context, t -> {
                            if (t.getPositiveBlockInfo().getBlockState() != null)
                                t.getNegativeBlockInfo().setBlockState(slabState);
                            else
                                t.getPositiveBlockInfo().setBlockState(slabState);
                        }))
                            return finishBlockPlacement(level, pos, slabState, player, originalStack);
                        return InteractionResult.PASS;
                    }
                }

                if (horizontalSlabSupport == null)
                    return InteractionResult.PASS;

                if (DSConfig.COMMON.isBlacklistedHorizontalSlab(state.getBlock()))
                    return InteractionResult.PASS;

                SlabType half = horizontalSlabSupport.getHalf(level, pos, state);
                if (half == SlabType.DOUBLE)
                    return InteractionResult.PASS;

                if (!DSConfig.COMMON.replaceSameSlab.get() && horizontalSlabItemSupport.equals(horizontalSlabSupport) && horizontalSlabSupport.areSame(level, pos, state, stack))
                    return InteractionResult.PASS;

                if ((face == Direction.UP && half == SlabType.BOTTOM) || (face == Direction.DOWN && half == SlabType.TOP)) {
                    state = prepareState(state);
                    if (!offset && activateBlock(level, pos, player, hand))
                        return InteractionResult.PASS;
                    BlockState slabState = getStateFromSupport(level, pos, player, hand, stack, half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM, horizontalSlabItemSupport);
                    if (DSConfig.COMMON.isBlacklistedHorizontalSlab(slabState.getBlock()))
                        return InteractionResult.PASS;

                    FluidState fluidstate = level.getFluidState(pos);

                    BlockState newState = Services.REGISTRIES.getBlocks().getDoubleSlabBlock().getStateForPlacement(context).setValue(DoubleSlabBlock.WATERLOGGED, fluidstate.getType() == Fluids.WATER && (horizontalSlabItemSupport.waterloggableWhenDouble(level, pos, slabState) || horizontalSlabSupport.waterloggableWhenDouble(level, pos, state)));

                    BlockEntity blockEntity = level.getBlockEntity(pos);

                    BlockState finalState1 = state;
                    if (placeSlab(level, pos, newState, offset ? context : getUseContext(player, hand, stack, pos), slab -> {
                        if (half == SlabType.BOTTOM) {
                            slab.getNegativeBlockInfo().setBlockState(finalState1);
                            slab.getNegativeBlockInfo().setBlockEntity(blockEntity);
                            slab.getPositiveBlockInfo().setBlockState(slabState);
                        } else {
                            slab.getNegativeBlockInfo().setBlockState(slabState);
                            slab.getPositiveBlockInfo().setBlockState(finalState1);
                            slab.getPositiveBlockInfo().setBlockEntity(blockEntity);
                        }
                    }))
                        return finishBlockPlacement(level, pos, slabState, player, originalStack);
                }
            }
        }
        return InteractionResult.PASS;
    }

}
