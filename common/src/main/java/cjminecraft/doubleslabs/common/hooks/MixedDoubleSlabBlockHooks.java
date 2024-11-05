package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.library.helpers.RayCastHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class MixedDoubleSlabBlockHooks extends DynamicSlabHooks {

    protected static @Nullable Half getHalfFromLookingAtBlock(final Player player, final BlockPos slabPos) {
        final BlockHitResult hitResult = RayCastHelper.getLookingAtBlock(player);

        if (hitResult.getType() != BlockHitResult.Type.BLOCK) {
            return null;
        }

        final Vec3 hitLocation = hitResult.getLocation();
        final double hitOffset = hitLocation.y - slabPos.getY();
        return hitOffset > 0.5 ? Half.TOP : Half.BOTTOM;
    }

    protected static <T> Optional<T> callOnLookingAtBlockState(BlockGetter blockGetter, BlockPos pos, Player player,
                                                               Function<BlockState, T> function) {
        @Nullable Half slabHalf = getHalfFromLookingAtBlock(player, pos);

        if (slabHalf == null) {
            return Optional.empty();
        }

        return callOnBlockState(blockGetter, pos, slabHalf, function);
    }

    public static Optional<Float> getDestroyProgress(Player player, BlockGetter blockGetter, BlockPos pos) {
        return callOnLookingAtBlockState(blockGetter, pos, player, state -> state.getDestroyProgress(player, blockGetter, pos))
                .or(() -> minFromBlockState(blockGetter, pos, state -> state.getDestroyProgress(player, blockGetter, pos)));
    }

}
