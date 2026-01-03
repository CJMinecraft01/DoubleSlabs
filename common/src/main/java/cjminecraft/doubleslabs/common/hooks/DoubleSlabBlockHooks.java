package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.common.Internal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DoubleSlabBlockHooks {

    public static boolean trySeparateDoubleSlab(final Player player, final Level level) {
        if (player.isCreative() && !player.isCrouching()) {
            return false;
        }

        final var hitResult = player.pick(player.blockInteractionRange(), 0F, false);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        return trySeparateDoubleSlab(player, level, (BlockHitResult) hitResult);
    }

    private static boolean trySeparateDoubleSlab(final Player player, final Level level, final BlockHitResult hitResult) {
        final var clickedPos = hitResult.getBlockPos();
        final var clickedSide = hitResult.getDirection();
        final var clickedState = level.getBlockState(clickedPos);

        final var slabHelper = Internal.getSlabHelper();

        final var optionalClickedSlabHelper = slabHelper.getHorizontalSlabHelper(clickedState);

        if (optionalClickedSlabHelper.isEmpty()) {
            return false;
        }

        final var clickedSlabHelper = optionalClickedSlabHelper.get();

        if (!clickedSlabHelper.isDoubleSlab(clickedState)) {
            return false;
        }

        // There are two possible states that we need to handle when figuring out which slab to keep

        // 1. If we click on the UP or DOWN face of a slab, then we remove the top or bottom and keep the other
        if (clickedSide.getAxis().isVertical()) {
            return separateDoubleSlab(level, player, clickedSlabHelper, clickedState, clickedPos, clickedSide == Direction.UP ? Half.POSITIVE : Half.NEGATIVE);
        }

        // 2. Otherwise, we use the hit vec to know if we are hitting the top or bottom slab
        final var normalizedHitY = hitResult.getLocation().y - clickedPos.getY();

        return separateDoubleSlab(level, player, clickedSlabHelper, clickedState, clickedPos, normalizedHitY > 0.5f ? Half.POSITIVE : Half.NEGATIVE);
    }

    private static boolean separateDoubleSlab(final Level level, final Player player, final IHorizontalSlabHelper slabHelper, final BlockState slabBlockState, final BlockPos slabPos, final Half halfToRemove) {
        final var stateToRemove = slabHelper.getStateForHalf(slabBlockState, halfToRemove);
        final var remainingSlabState = slabHelper.getStateForHalf(slabBlockState, halfToRemove.getOpposite());

        if (!level.setBlock(slabPos, remainingSlabState, 3)) {
            return false;
        }

        player.awardStat(Stats.BLOCK_MINED.get(stateToRemove.getBlock()));
        level.levelEvent(2001, slabPos, Block.getId(stateToRemove));
        player.causeFoodExhaustion(0.005F);

        if (!player.isCreative()) {
            Block.dropResources(stateToRemove, level, slabPos);
        }

        stateToRemove.onRemove(level, slabPos, Blocks.AIR.defaultBlockState(), false);

        return true;
    }

}
