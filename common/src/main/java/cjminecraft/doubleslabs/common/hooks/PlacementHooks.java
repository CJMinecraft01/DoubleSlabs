package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.helpers.ISlabHelper;
import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.Internal;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Optional;

public class PlacementHooks {

    public static Optional<InteractionResult> useItemOnBlock(UseOnContext context) {
        @Nullable Player player = context.getPlayer();
        if (player == null) {
            return Optional.empty();
        }

        ItemStack itemInHand = context.getItemInHand();
        if (itemInHand.isEmpty()) {
            return Optional.empty();
        }

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        BlockState clickedState = level.getBlockState(clickedPos);

        InteractionHand hand = context.getHand();

        BlockHitResult blockHitResult = context.getHitResult();

        return useItemOnBlock(level, clickedState, clickedPos, clickedFace, player, itemInHand, hand, blockHitResult);
    }

    private static Optional<InteractionResult> useItemOnBlock(final Level level,
                                                              final BlockState clickedBlockState,
                                                              final BlockPos clickedPos,
                                                              final Direction clickedFace, final Player player,
                                                              final ItemStack itemInHand,
                                                              final InteractionHand hand,
                                                              final BlockHitResult blockHitResult) {
        final ISlabHelper slabHelper = Internal.getSlabHelper();

        Optional<IHorizontalSlabHelper> optionalItemInHandSlabHelper = slabHelper.getHorizontalSlabHelper(itemInHand);

        // If the held item is a horizontal slab
        if (optionalItemInHandSlabHelper.isPresent()) {
            final IHorizontalSlabHelper itemInHandSlabHelper = optionalItemInHandSlabHelper.get();

            // There are two possible states in which placing a horizontal slab will create a dynamic double slab

            final Optional<IHorizontalSlabHelper> optionalClickedBlockSlabHelper =
                    slabHelper.getHorizontalSlabHelper(clickedBlockState);
            // 1. If we click on the UP or DOWN face of a slab (so the slab would be inside the same block)
            if (clickedFace.getAxis().isVertical() && optionalClickedBlockSlabHelper.isPresent()) {
                final IHorizontalSlabHelper clickedBlockSlabHelper = optionalClickedBlockSlabHelper.get();

                Half half = clickedBlockSlabHelper.getHalf(level, clickedPos, clickedBlockState);
                // Check that the side clicked is the side that would place the slab within the same block
                if ((half == Half.BOTTOM && clickedFace == Direction.UP) || (half == Half.TOP && clickedFace == Direction.DOWN)) {
                    return tryCombineHorizontalSlabs(level, clickedBlockState, clickedPos, player, itemInHand, hand,
                            blockHitResult, clickedBlockSlabHelper, itemInHandSlabHelper);
                }
            }

            final BlockPos posRelativeToClickedFace = clickedPos.relative(clickedFace);
            final BlockState stateRelativeToClickedFace = level.getBlockState(posRelativeToClickedFace);
            final Optional<IHorizontalSlabHelper> optionalSlabHelperRelativeToClickedFace =
                    slabHelper.getHorizontalSlabHelper(stateRelativeToClickedFace);
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
                                                                             final IHorizontalSlabHelper slabBlockHelper, final IHorizontalSlabHelper slabItemHelper) {
        final ISlabHelper slabHelper = Internal.getSlabHelper();

        // If the slab item and slab block are the same type of slab then use the default behaviour
        if (slabHelper.areSameTypeOfSlab(slabBlockState, slabBlockHelper, itemInHand, slabItemHelper)) {
            return Optional.empty();
        }

        BlockPlaceContext blockPlaceContext = new BlockPlaceContext(player, hand, itemInHand, blockHitResult);
        @Nullable BlockState stateFromSlabItem = slabItemHelper.getStateFromStack(itemInHand, blockPlaceContext);

        if (stateFromSlabItem == null) {
            return Optional.empty();
        }

        Half slabBlockHalf = slabBlockHelper.getHalf(level, slabPos, slabBlockState);
        Half slabToPlaceHalf = slabBlockHalf == Half.TOP ? Half.BOTTOM : Half.TOP;

        BlockState slabToPlaceState = slabItemHelper.getStateForHalf(level, slabPos, stateFromSlabItem,
                slabToPlaceHalf);

        @Nullable BlockEntity existingBlockEntity = level.getBlockEntity(slabPos);

        BlockState dynamicDoubleSlabState = DSBlocks.MIXED_SLAB.get().defaultBlockState();

        if (!level.setBlock(slabPos, dynamicDoubleSlabState, 3)) {
            return Optional.empty();
        }

        if (player instanceof ServerPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, slabPos, itemInHand);
        }

        SoundType soundType = slabToPlaceState.getSoundType();
        level.playSound(player, slabPos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, slabPos, GameEvent.Context.of(player, slabToPlaceState));

        itemInHand.shrink(1);

        Optional<? extends IDynamicSlabStateContainer> optionalDynamicSlabBlockEntity = level.getBlockEntity(slabPos,
                DSBlockEntities.DYNAMIC_SLAB.get());

        return optionalDynamicSlabBlockEntity.map(dynamicSlabBlockEntity -> {
            dynamicSlabBlockEntity.setBlockState(slabBlockHalf, slabBlockState);
            dynamicSlabBlockEntity.setBlockEntity(slabBlockHalf, existingBlockEntity);
            dynamicSlabBlockEntity.setBlockState(slabToPlaceHalf, slabToPlaceState);

            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        });
    }

}
