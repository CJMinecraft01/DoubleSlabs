package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class MixedDoubleSlabBlockHooks extends DynamicSlabHooks {

    protected static Half getOpposite(Half half) {
        return switch (half) {
            case TOP -> Half.BOTTOM;
            case BOTTOM -> Half.TOP;
        };
    }

    protected static @Nullable Half getHalfFromLookingAtBlock(final Player player, final BlockPos slabPos) {
        final HitResult hitResult = player.pick(player.blockInteractionRange(), 0F, false);

        if (hitResult.getType() != BlockHitResult.Type.BLOCK) {
            return null;
        }

        final BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();

        // If the hit block pos is not the same block as the slab then we cannot get the half
        if (!slabPos.equals(hitPos)) {
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

    // The result of removeBlock is whether the block is considered to have been removed.
    public static boolean removeBlock(BlockState state, Level level, BlockPos pos, Player player, FluidState fluidState, boolean willHarvest) {
        // If we will harvest the block then destroy the block using player destroy
        if (willHarvest) {
            return true;
        }

        // If the player is crouching in creative then break the slabs separately
        if (player.isCreative() && player.isCrouching()) {
            // We call player destroy manually here since it is not called when the player is in creative
            playerDestroy(player, level, pos, state, level.getBlockEntity(pos), player.getMainHandItem());
            return true;
        }

        // Default behaviour
        return level.isClientSide() ? level.setBlock(pos, fluidState.createLegacyBlock(), 11) : level.removeBlock(pos, false);
    }

    public static void playerDestroy(Player player, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        @Nullable Half halfToRemove = getHalfFromLookingAtBlock(player, pos);

        if (halfToRemove == null || !(blockEntity instanceof IDynamicSlabStateContainer container)) {
            player.awardStat(Stats.BLOCK_MINED.get(DSBlocks.DOUBLE_SLAB.get()));
            player.causeFoodExhaustion(0.005F);
            Block.dropResources(state, level, pos, blockEntity, player, tool);
        } else {
            Half halfToKeep = getOpposite(halfToRemove);

            container.runOnStateContainer(halfToRemove, slabContainer -> {
                if (!slabContainer.hasBlockState()) {
                    return;
                }

                final BlockState slabState = slabContainer.getBlockState();

                player.awardStat(Stats.BLOCK_MINED.get(slabState.getBlock()));
                level.levelEvent(2001, pos, Block.getId(slabState));
                player.causeFoodExhaustion(0.005F);

                if (!player.isCreative()) {
                    Block.dropResources(slabState, level, pos, slabContainer.getBlockEntity(), player, tool);
                }

                slabState.onRemove(level, pos, Blocks.AIR.defaultBlockState(), false);
            });

            container.runOnStateContainer(halfToKeep, slabContainer -> {
                if (!slabContainer.hasBlockState()) {
                    return;
                }

                final BlockState slabState = slabContainer.getBlockState();

                level.setBlock(pos, slabState, level.isClientSide() ? 11 : 3);

                if (slabContainer.hasBlockEntity()) {
                    level.setBlockEntity(Objects.requireNonNull(slabContainer.getBlockEntity()));
                } else {
                    level.removeBlockEntity(pos);
                }
            });
        }
    }

}
