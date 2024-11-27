package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class MixedDoubleSlabBlockHooks extends DynamicSlabHooks {

    protected static @Nullable Half getHalfFromHitResult(final HitResult hitResult, final BlockPos slabPos) {
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

    protected static @Nullable Half getHalfFromLookingAtBlock(final Player player, final BlockPos slabPos) {
        final HitResult hitResult = player.pick(player.blockInteractionRange(), 0F, false);

        return getHalfFromHitResult(hitResult, slabPos);
    }

    protected static void runOnLookingAtBlockState(BlockGetter blockGetter, BlockPos pos, HitResult hitResult, Consumer<BlockState> consumer) {
        @Nullable Half slabHalf = getHalfFromHitResult(hitResult, pos);

        if (slabHalf == null) {
            return;
        }

        runOnBlockState(blockGetter, pos, slabHalf, consumer);
    }

    protected static <T> Optional<T> callOnLookingAtBlockState(BlockGetter blockGetter, BlockPos pos, HitResult hitResult, Function<BlockState, T> function) {
        @Nullable Half slabHalf = getHalfFromHitResult(hitResult, pos);

        if (slabHalf == null) {
            return Optional.empty();
        }

        return callOnBlockState(blockGetter, pos, slabHalf, function);
    }

    protected static <T> Optional<T> callOnLookingAtBlockState(BlockGetter blockGetter, BlockPos pos, Player player, Function<BlockState, T> function) {
        @Nullable Half slabHalf = getHalfFromLookingAtBlock(player, pos);

        if (slabHalf == null) {
            return Optional.empty();
        }

        return callOnBlockState(blockGetter, pos, slabHalf, function);
    }

    public static Optional<Float> getDestroyProgress(Player player, BlockGetter blockGetter, BlockPos pos) {
        return callOnLookingAtBlockState(blockGetter, pos, player, state -> state.getDestroyProgress(player, blockGetter, pos)).or(() -> minFromBlockState(blockGetter, pos, state -> state.getDestroyProgress(player, blockGetter, pos)));
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
            player.awardStat(Stats.BLOCK_MINED.get(DSBlocks.MIXED_SLAB.get()));
            player.causeFoodExhaustion(0.005F);
            Block.dropResources(state, level, pos, blockEntity, player, tool);
        } else {
            Half halfToKeep = halfToRemove.getOpposite();

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

    public static ItemStack getCloneItemStack(LevelReader level, BlockPos pos, HitResult hitResult) {
        return callOnLookingAtBlockState(level, pos, hitResult, state -> state.getBlock().getCloneItemStack(level, pos, state))
                .orElse(ItemStack.EMPTY);
    }

    public static Optional<SoundType> getSoundType(BlockGetter blockGetter, BlockPos pos, @Nullable Entity entity) {
        if (entity instanceof Player player) {
            // We first assume that we are destroying a block and so get the state based on what the player is looking at
            Optional<SoundType> destroyBlockSound = callOnLookingAtBlockState(blockGetter, pos, player, BlockBehaviour.BlockStateBase::getSoundType);
            if (destroyBlockSound.isPresent()) {
                return destroyBlockSound;
            }
            // If the player is not looking at this slab block then treat it like all other entities
        }

        // If we have an entity, get the sound type for the top slab
        if (entity != null) {
            return callOnBlockState(blockGetter, pos, Half.TOP, BlockBehaviour.BlockStateBase::getSoundType);
        }

        return Optional.empty();
    }

    public static Optional<BlockParticleOption> getParticleForTopSlab(BlockGetter blockGetter, BlockPos pos) {
        return callOnBlockState(blockGetter, pos, Half.TOP, state -> new BlockParticleOption(ParticleTypes.BLOCK, state));
    }

    public static boolean propagatesSkylightDown(BlockGetter blockGetter, BlockPos pos) {
        return requireBothStates(blockGetter, pos, state -> state.propagatesSkylightDown(blockGetter, pos));
    }

    public static boolean fallOn(Level level, BlockPos pos, Entity entity, float fallDistance) {
        return callOnBlockState(level, pos, Half.TOP, state -> {
            state.getBlock().fallOn(level, state, pos, entity, fallDistance);
            return true;
        }).orElse(false);
    }

    public static boolean updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockPos pos = entity.blockPosition().below();

        if (!blockGetter.getBlockState(pos).is(DSBlocks.MIXED_SLAB.get())) {
            return false;
        }

        return callOnBlockState(blockGetter, pos, Half.TOP, state -> {
            state.getBlock().updateEntityAfterFallOn(blockGetter, entity);
            return true;
        }).orElse(false);
    }

}
