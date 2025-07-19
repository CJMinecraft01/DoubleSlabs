package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class VerticalSlabBlockHooks extends DynamicSlabBlockHooks {

    protected static @Nullable Half getHalfFromHitResult(final BlockState verticalSlabState, final HitResult hitResult, final BlockPos slabPos) {
        if (hitResult.getType() != BlockHitResult.Type.BLOCK) {
            return null;
        }

        final var hitPos = ((BlockHitResult) hitResult).getBlockPos();

        // If the hit block pos is not the same block as the slab then we cannot get the half
        if (!slabPos.equals(hitPos)) {
            return null;
        }

        final var type = verticalSlabState.getValue(VerticalSlabBlock.TYPE);

        if (type != VerticalSlabType.DOUBLE) {
            return type.getHalf();
        }

        final var axis = verticalSlabState.getValue(VerticalSlabBlock.AXIS);

        final var hitLocation = hitResult.getLocation();
        final var hitOffset = hitLocation.get(axis) - slabPos.get(axis);

        return hitOffset > 0.5 ? Half.POSITIVE : Half.NEGATIVE;
    }

    protected static @Nullable Half getHalfFromLookingAtBlock(final BlockState verticalSlabState, final Player player, final BlockPos slabPos) {
        final var hitResult = player.pick(player.blockInteractionRange(), 0F, false);

        return getHalfFromHitResult(verticalSlabState, hitResult, slabPos);
    }

    protected static <T> Optional<T> callOnLookingAtBlockState(final BlockGetter blockGetter, final BlockState state, final BlockPos pos, final HitResult hitResult, final Function<BlockState, T> function) {
        final @Nullable Half slabHalf = getHalfFromHitResult(state, hitResult, pos);

        if (slabHalf == null) {
            return Optional.empty();
        }

        return callOnBlockState(blockGetter, pos, slabHalf, function);
    }

    protected static <T> Optional<T> callOnLookingAtBlockState(final BlockGetter blockGetter, final BlockState state, final BlockPos pos, final Player player, final Function<BlockState, T> function) {
        final @Nullable Half slabHalf = getHalfFromLookingAtBlock(state, player, pos);

        if (slabHalf == null) {
            return Optional.empty();
        }

        return callOnBlockState(blockGetter, pos, slabHalf, function);
    }

    public static Optional<Float> getDestroyProgress(Player player, BlockGetter blockGetter, BlockState state, BlockPos pos) {
        return callOnLookingAtBlockState(blockGetter, state, pos, player, s -> s.getDestroyProgress(player, blockGetter, pos)).or(() -> minFromBlockState(blockGetter, pos, s -> s.getDestroyProgress(player, blockGetter, pos)));
    }

}
