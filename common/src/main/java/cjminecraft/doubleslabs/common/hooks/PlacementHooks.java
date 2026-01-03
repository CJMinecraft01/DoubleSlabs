package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.helpers.IVerticalSlabHelper;
import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.VerticalSlabState;
import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.common.init.DSBlocks;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Optional;

public class PlacementHooks {

    public static Optional<InteractionResult> useItemOnBlock(final @Nullable Player player,
                                                             final Level level,
                                                             final InteractionHand hand,
                                                             final BlockHitResult hitResult) {
        if (player == null) {
            return Optional.empty();
        }

        final var itemInHand = player.getItemInHand(hand);
        if (itemInHand.isEmpty()) {
            return Optional.empty();
        }

        final var clickedPos = hitResult.getBlockPos();
        final var clickedFace = hitResult.getDirection();
        final var clickedState = level.getBlockState(clickedPos);

        return useItemOnBlock(level, clickedState, clickedPos, clickedFace, player, itemInHand, hand, hitResult);
    }

    private static Optional<InteractionResult> useItemOnBlock(final Level level,
                                                                  final BlockState clickedBlockState,
                                                                  final BlockPos clickedPos,
                                                                  final Direction clickedFace,
                                                                  final Player player,
                                                                  final ItemStack itemInHand,
                                                                  final InteractionHand hand,
                                                                  final BlockHitResult blockHitResult) {
        return tryUseHorizontalSlab(level, clickedBlockState, clickedPos, clickedFace, player, itemInHand, hand, blockHitResult)
                .or(() -> tryUseVerticalSlab(level, clickedBlockState, clickedPos, clickedFace, player, itemInHand, hand, blockHitResult));
    }

    private static Optional<InteractionResult> tryUseVerticalSlab(final Level level,
                                                                        final BlockState clickedBlockState,
                                                                        final BlockPos clickedPos,
                                                                        final Direction clickedFace,
                                                                        final Player player,
                                                                        final ItemStack itemInHand,
                                                                        final InteractionHand hand,
                                                                        final BlockHitResult blockHitResult) {
        final var slabHelper = Internal.getSlabHelper();

        final var optionalItemInHandSlabHelper = slabHelper.getVerticalSlabHelper(itemInHand);

        // If the held item is a vertical slab
        if (optionalItemInHandSlabHelper.isPresent()) {
            final var itemInHandSlabHelper = optionalItemInHandSlabHelper.get();

            // There are two possible states in which placing a vertical slab will create a dynamic double vertical slab

            // 1. If we click on a horizontal face of a slab (so the slab would place in the same block)
            if (clickedFace.getAxis().isHorizontal()) {
                // 1a. The vertical slab clicked is a dynamic one so try to merge them
                if (clickedBlockState.is(DSBlocks.VERTICAL_SLAB.get())) {
                    final var type = clickedBlockState.getValue(VerticalSlabBlock.TYPE);
                    final var axis = clickedBlockState.getValue(VerticalSlabBlock.AXIS);

                    // If we are clicking on the side of a double slab, try to place relative
                    // Otherwise, try to combine the slab
                    if (type != VerticalSlabType.DOUBLE) {
                        final var facingDirection = Direction.fromAxisAndDirection(axis, type.toAxisDirection());

                        // Check that the side clicked is the side that would place the slab within the same block
                        if (facingDirection == clickedFace.getOpposite()) {
                            return tryCombineDynamicVerticalSlab(level, clickedBlockState, clickedPos, player,
                                    itemInHand, hand, blockHitResult, itemInHandSlabHelper);
                        }
                    }
                }

                final var optionalClickedBlockSlabHelper = slabHelper.getVerticalSlabHelper(clickedBlockState);

                // 1b. The vertical slab is not dynamic so create a dynamic vertical slab
                if (optionalClickedBlockSlabHelper.isPresent()) {
                    final var clickedBlockSlabHelper = optionalClickedBlockSlabHelper.get();
                    final var clickedVerticalSlabState = clickedBlockSlabHelper.getVerticalSlabState(level, clickedPos, clickedBlockState);

                    // Check that the side clicked is the side that would place the slab within the same block and
                    // the slab is not a double slab
                    if (!clickedVerticalSlabState.isDouble() && clickedVerticalSlabState.getFacingDirection() == clickedFace.getOpposite()) {
                        return tryCombineVerticalSlabs(level, clickedBlockState, clickedPos, player, itemInHand, hand,
                                blockHitResult, clickedBlockSlabHelper, itemInHandSlabHelper);
                    }
                }
            }

            // 2. If we click on a side of the block which will cause two slabs to be merged
            final var posRelativeToClickedFace = clickedPos.relative(clickedFace);
            final var stateRelativeToClickedFace = level.getBlockState(posRelativeToClickedFace);

            // 2a. The vertical slab is a dynamic one so merge them
            if (stateRelativeToClickedFace.is(DSBlocks.VERTICAL_SLAB.get())) {
                return tryCombineDynamicVerticalSlab(level, stateRelativeToClickedFace, posRelativeToClickedFace,
                        player, itemInHand, hand, blockHitResult, itemInHandSlabHelper);
            }

            final var optionalSlabHelperRelativeToClickedFace = slabHelper.getVerticalSlabHelper(stateRelativeToClickedFace);
            // 2b. The vertical slab is not dynamic so create a dynamic one
            if (optionalSlabHelperRelativeToClickedFace.isPresent()) {
                return tryCombineVerticalSlabs(level, stateRelativeToClickedFace, posRelativeToClickedFace, player,
                        itemInHand, hand, blockHitResult, optionalSlabHelperRelativeToClickedFace.get(),
                        itemInHandSlabHelper);
            }
        }

        return Optional.empty();
    }

    private static Optional<InteractionResult> tryCombineDynamicVerticalSlab(final Level level,
                                                                                 final BlockState dynamicVerticalSlabState,
                                                                                 final BlockPos slabPos,
                                                                                 final Player player,
                                                                                 final ItemStack itemInHand,
                                                                                 final InteractionHand hand,
                                                                                 final BlockHitResult blockHitResult,
                                                                                 final IVerticalSlabHelper slabItemHelper) {
        final var blockPlaceContext = new BlockPlaceContext(player, hand, itemInHand, blockHitResult);
        final @Nullable BlockState stateFromSlabItem = slabItemHelper.getStateFromStack(itemInHand, blockPlaceContext);

        if (stateFromSlabItem == null) {
            return Optional.empty();
        }

        final var axis = dynamicVerticalSlabState.getValue(VerticalSlabBlock.AXIS);
        final var type = dynamicVerticalSlabState.getValue(VerticalSlabBlock.TYPE);

        final var slabToPlaceType = type.getOpposite();
        final var slabToPlaceHalf = slabToPlaceType.getHalf();
        final var verticalSlabStateToPlace = new VerticalSlabState(axis, slabToPlaceType);

        final var slabToPlaceState = slabItemHelper.getStateForVerticalSlabState(level, slabPos, stateFromSlabItem, verticalSlabStateToPlace);

        if (!level.setBlockAndUpdate(slabPos, dynamicVerticalSlabState.setValue(VerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE))) {
            return Optional.empty();
        }

        handleBlockPlaced(level, player, slabToPlaceState, slabPos, itemInHand);

        final var optionalDynamicSlabBlockEntity = level.getBlockEntity(slabPos, DSBlockEntities.DYNAMIC_SLAB.get());

        return optionalDynamicSlabBlockEntity.map(dynamicSlabBlockEntity -> {
            dynamicSlabBlockEntity.setBlockState(slabToPlaceHalf, slabToPlaceState);

            if (slabToPlaceState.hasBlockEntity()) {
                final var slabToPlaceBlockEntity = ((EntityBlock) slabToPlaceState.getBlock()).newBlockEntity(slabPos, slabToPlaceState);
                dynamicSlabBlockEntity.setBlockEntity(slabToPlaceHalf, slabToPlaceBlockEntity);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        });
    }

    private static Optional<InteractionResult> tryCombineVerticalSlabs(final Level level,
                                                                           final BlockState slabBlockState,
                                                                           final BlockPos slabPos,
                                                                           final Player player,
                                                                           final ItemStack itemInHand,
                                                                           final InteractionHand hand,
                                                                           final BlockHitResult blockHitResult,
                                                                           final IVerticalSlabHelper slabBlockHelper,
                                                                           final IVerticalSlabHelper slabItemHelper) {
        final var slabHelper = Internal.getSlabHelper();

        final var slabVerticalSlabState = slabBlockHelper.getVerticalSlabState(level, slabPos, slabBlockState);

        // If the slab block is a double slab then ignore
        if (slabVerticalSlabState.isDouble()) {
            return Optional.empty();
        }

        // If the slab item and slab block are the same type of slab then use the default behaviour
        if (slabHelper.areSameTypeOfSlab(slabBlockState, slabBlockHelper, itemInHand, slabItemHelper)) {
            return Optional.empty();
        }

        final var blockPlaceContext = new BlockPlaceContext(player, hand, itemInHand, blockHitResult);
        final @Nullable BlockState stateFromSlabItem = slabItemHelper.getStateFromStack(itemInHand, blockPlaceContext);

        if (stateFromSlabItem == null) {
            return Optional.empty();
        }

        final var slabBlockType = slabVerticalSlabState.type();
        final var slabBlockHalf = slabBlockType.getHalf();
        final var slabToPlaceType = slabVerticalSlabState.type().getOpposite();
        final var slabToPlaceHalf = slabToPlaceType.getHalf();
        final var verticalSlabStateToPlace = new VerticalSlabState(slabVerticalSlabState.axis(), slabToPlaceType);

        final var slabToPlaceState = slabItemHelper.getStateForVerticalSlabState(level, slabPos, stateFromSlabItem, verticalSlabStateToPlace);

        final @Nullable BlockEntity existingBlockEntity = level.getBlockEntity(slabPos);

        final var dynamicVerticalSlabState = DSBlocks.VERTICAL_SLAB.get().defaultBlockState()
                .setValue(VerticalSlabBlock.AXIS, slabVerticalSlabState.axis())
                .setValue(VerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE);

        if (!level.setBlockAndUpdate(slabPos, dynamicVerticalSlabState)) {
            return Optional.empty();
        }

        handleBlockPlaced(level, player, slabToPlaceState, slabPos, itemInHand);

        final var optionalDynamicSlabBlockEntity = level.getBlockEntity(slabPos, DSBlockEntities.DYNAMIC_SLAB.get());

        return optionalDynamicSlabBlockEntity.map(dynamicSlabBlockEntity -> {
            dynamicSlabBlockEntity.setBlockState(slabBlockHalf, slabBlockState);
            dynamicSlabBlockEntity.setBlockEntity(slabBlockHalf, existingBlockEntity);
            dynamicSlabBlockEntity.setBlockState(slabToPlaceHalf, slabToPlaceState);

            if (slabToPlaceState.hasBlockEntity()) {
                final var slabToPlaceBlockEntity = ((EntityBlock) slabToPlaceState.getBlock()).newBlockEntity(slabPos, slabToPlaceState);
                dynamicSlabBlockEntity.setBlockEntity(slabToPlaceHalf, slabToPlaceBlockEntity);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        });
    }

    private static Optional<InteractionResult> tryUseHorizontalSlab(final Level level,
                                                                        final BlockState clickedBlockState,
                                                                        final BlockPos clickedPos,
                                                                        final Direction clickedFace,
                                                                        final Player player,
                                                                        final ItemStack itemInHand,
                                                                        final InteractionHand hand,
                                                                        final BlockHitResult blockHitResult) {
        final var slabHelper = Internal.getSlabHelper();

        final var optionalItemInHandSlabHelper = slabHelper.getHorizontalSlabHelper(itemInHand);

        // If the held item is a horizontal slab
        if (optionalItemInHandSlabHelper.isPresent()) {
            final var itemInHandSlabHelper = optionalItemInHandSlabHelper.get();

            // There are two possible states in which placing a horizontal slab will create a dynamic double slab

            final var optionalClickedBlockSlabHelper = slabHelper.getHorizontalSlabHelper(clickedBlockState);
            // 1. If we click on the UP or DOWN face of a slab (so the slab would be inside the same block)
            if (clickedFace.getAxis().isVertical() && optionalClickedBlockSlabHelper.isPresent()) {
                final var clickedBlockSlabHelper = optionalClickedBlockSlabHelper.get();

                // If we are clicking on the top or bottom of a double slab, try to place relative
                // Otherwise, try to combine the slabs
                if (!clickedBlockSlabHelper.isDoubleSlab(clickedBlockState)) {
                    final var half = clickedBlockSlabHelper.getHalf(clickedBlockState);
                    // Check that the side clicked is the side that would place the slab within the same block
                    if ((half == Half.NEGATIVE && clickedFace == Direction.UP) || (half == Half.POSITIVE && clickedFace == Direction.DOWN)) {
                        return tryCombineHorizontalSlabs(level, clickedBlockState, clickedPos, player, itemInHand, hand,
                                blockHitResult, clickedBlockSlabHelper, itemInHandSlabHelper);
                    }
                }
            }

            final var posRelativeToClickedFace = clickedPos.relative(clickedFace);
            final var stateRelativeToClickedFace = level.getBlockState(posRelativeToClickedFace);
            final var optionalSlabHelperRelativeToClickedFace = slabHelper.getHorizontalSlabHelper(stateRelativeToClickedFace);
            // 2. If we click on the side of a block where there is a horizontal slab opposite the face
            if (optionalSlabHelperRelativeToClickedFace.isPresent()) {
                return tryCombineHorizontalSlabs(level, stateRelativeToClickedFace, posRelativeToClickedFace, player,
                        itemInHand, hand, blockHitResult, optionalSlabHelperRelativeToClickedFace.get(),
                        itemInHandSlabHelper);
            }
        }

        return Optional.empty();
    }

    private static Optional<InteractionResult> tryCombineHorizontalSlabs(final Level level,
                                                                         final BlockState slabBlockState,
                                                                         final BlockPos slabPos,
                                                                         final Player player,
                                                                         final ItemStack itemInHand,
                                                                         final InteractionHand hand,
                                                                         final BlockHitResult blockHitResult,
                                                                         final IHorizontalSlabHelper slabBlockHelper,
                                                                         final IHorizontalSlabHelper slabItemHelper) {
        final var slabHelper = Internal.getSlabHelper();

        // If the slab block is a double slab then ignore
        if (slabBlockHelper.isDoubleSlab(slabBlockState)) {
            return Optional.empty();
        }

        // If the slab item and slab block are the same type of slab then use the default behaviour
        if (slabHelper.areSameTypeOfSlab(slabBlockState, slabBlockHelper, itemInHand, slabItemHelper)) {
            return Optional.empty();
        }

        final var blockPlaceContext = new BlockPlaceContext(player, hand, itemInHand, blockHitResult);
        final @Nullable BlockState stateFromSlabItem = slabItemHelper.getStateFromStack(itemInHand, blockPlaceContext);

        if (stateFromSlabItem == null) {
            return Optional.empty();
        }

        final var slabBlockHalf = slabBlockHelper.getHalf(slabBlockState);
        final var slabToPlaceHalf = slabBlockHalf.getOpposite();

        final var slabToPlaceState = slabItemHelper.getStateForHalf(stateFromSlabItem, slabToPlaceHalf);

        final var bothSlabsOcclude = slabToPlaceState.canOcclude() && slabBlockState.canOcclude();

        final @Nullable BlockEntity existingBlockEntity = level.getBlockEntity(slabPos);

        final var dynamicDoubleSlabState = (bothSlabsOcclude ? DSBlocks.MIXED_SLAB.get() : DSBlocks.TRANSPARENT_MIXED_SLAB.get())
                .defaultBlockState();

        if (!level.setBlock(slabPos, dynamicDoubleSlabState, 3)) {
            return Optional.empty();
        }

        handleBlockPlaced(level, player, slabToPlaceState, slabPos, itemInHand);

        final var optionalDynamicSlabBlockEntity = level.getBlockEntity(slabPos, DSBlockEntities.DYNAMIC_SLAB.get());

        return optionalDynamicSlabBlockEntity.map(dynamicSlabBlockEntity -> {
            dynamicSlabBlockEntity.setBlockState(slabBlockHalf, slabBlockState);
            dynamicSlabBlockEntity.setBlockEntity(slabBlockHalf, existingBlockEntity);
            dynamicSlabBlockEntity.setBlockState(slabToPlaceHalf, slabToPlaceState);

            if (slabToPlaceState.hasBlockEntity()) {
                final var slabToPlaceBlockEntity = ((EntityBlock) slabToPlaceState.getBlock()).newBlockEntity(slabPos, slabToPlaceState);
                dynamicSlabBlockEntity.setBlockEntity(slabToPlaceHalf, slabToPlaceBlockEntity);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        });
    }

    private static void handleBlockPlaced(final Level level,
                                          final Player player,
                                          final BlockState statePlaced,
                                          final BlockPos pos,
                                          final ItemStack itemInHand) {
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, pos, itemInHand);
        }

        final var soundType = statePlaced.getSoundType();
        level.playSound(player, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, statePlaced));

        if (!player.isCreative()) {
            itemInHand.shrink(1);
        }
    }

}
