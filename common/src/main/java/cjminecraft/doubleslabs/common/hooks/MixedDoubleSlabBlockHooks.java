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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class MixedDoubleSlabBlockHooks extends DynamicSlabBlockHooks {

    protected static @Nullable Half getHalfFromHitResult(final HitResult hitResult, final BlockPos slabPos) {
        if (hitResult.getType() != BlockHitResult.Type.BLOCK) {
            return null;
        }

        final var hitPos = ((BlockHitResult) hitResult).getBlockPos();

        // If the hit block pos is not the same block as the slab then we cannot get the half
        if (!slabPos.equals(hitPos)) {
            return null;
        }

        final var hitLocation = hitResult.getLocation();
        final var hitOffset = hitLocation.y - slabPos.getY();
        return hitOffset > 0.5 ? Half.POSITIVE : Half.NEGATIVE;
    }

    protected static @Nullable Half getHalfFromLookingAtBlock(final Player player, final BlockPos slabPos) {
        final var hitResult = player.pick(player.blockInteractionRange(), 0F, false);

        return getHalfFromHitResult(hitResult, slabPos);
    }

    private static Half getHalfFromPlayerUsingCollision(final Player player, final VoxelShape collisionShape, final BlockPos slabPos) {
        final var clipStart = player.getEyePosition();
        final var clipEnd = player.getEyePosition().add(player.getLookAngle().scale(player.blockInteractionRange()));

        final var hitResult = Objects.requireNonNull(collisionShape.clip(clipStart, clipEnd, slabPos));

        return Objects.requireNonNull(getHalfFromHitResult(hitResult, slabPos));
    }

    protected static void runOnLookingAtBlockState(BlockGetter blockGetter, BlockPos pos, HitResult hitResult, Consumer<BlockState> consumer) {
        final @Nullable Half slabHalf = getHalfFromHitResult(hitResult, pos);

        if (slabHalf == null) {
            return;
        }

        runOnBlockState(blockGetter, pos, slabHalf, consumer);
    }

    protected static <T> Optional<T> callOnLookingAtBlockState(BlockGetter blockGetter, BlockPos pos, HitResult hitResult, Function<BlockState, T> function) {
        final @Nullable Half slabHalf = getHalfFromHitResult(hitResult, pos);

        if (slabHalf == null) {
            return Optional.empty();
        }

        return callOnBlockState(blockGetter, pos, slabHalf, function);
    }

    protected static <T> Optional<T> callOnLookingAtBlockState(BlockGetter blockGetter, BlockPos pos, Player player, Function<BlockState, T> function) {
        final @Nullable Half slabHalf = getHalfFromLookingAtBlock(player, pos);

        if (slabHalf == null) {
            return Optional.empty();
        }

        return callOnBlockState(blockGetter, pos, slabHalf, function);
    }

    public static Optional<Float> getDestroyProgress(Player player, BlockGetter blockGetter, BlockPos pos) {
        return callOnLookingAtBlockState(blockGetter, pos, player, state -> state.getDestroyProgress(player, blockGetter, pos)).or(() -> minFromBlockState(blockGetter, pos, state -> state.getDestroyProgress(player, blockGetter, pos)));
    }

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

        // Return false to signify that we want to call the super method
        return false;
    }

    public static void playerDestroy(Player player, Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        // The block has been destroyed at this point so the raytrace results will be incorrect
        // Hence we use the block pos and eye pos to work out which half we are looking at

        final var halfToRemove = getHalfFromPlayerUsingCollision(player, state.getCollisionShape(level, pos), pos);

        if (!(blockEntity instanceof IDynamicSlabStateContainer container)) {
            player.causeFoodExhaustion(0.005F);
            Block.dropResources(state, level, pos, blockEntity, player, tool);
        } else {
            final var halfToKeep = halfToRemove.getOpposite();

            destroyHalf(container, player, level, pos, tool, halfToRemove);

            container.runOnStateContainer(halfToKeep, slabContainer -> {
                if (!slabContainer.hasBlockState()) {
                    return;
                }

                final var slabState = slabContainer.getBlockState();

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
            final var destroyBlockSound = callOnLookingAtBlockState(blockGetter, pos, player, BlockBehaviour.BlockStateBase::getSoundType);
            if (destroyBlockSound.isPresent()) {
                return destroyBlockSound;
            }
            // If the player is not looking at this slab block then treat it like all other entities
        }

        // If we have an entity, get the sound type for the top slab
        if (entity != null) {
            return callOnBlockState(blockGetter, pos, Half.POSITIVE, BlockBehaviour.BlockStateBase::getSoundType);
        }

        return Optional.empty();
    }

    public static Optional<BlockParticleOption> getParticleForTopSlab(BlockGetter blockGetter, BlockPos pos) {
        return callOnBlockState(blockGetter, pos, Half.POSITIVE, state -> new BlockParticleOption(ParticleTypes.BLOCK, state));
    }

    public static boolean propagatesSkylightDown(BlockGetter blockGetter, BlockPos pos) {
        return requireBothStates(blockGetter, pos, state -> state.propagatesSkylightDown(blockGetter, pos));
    }

    public static boolean fallOn(Level level, BlockPos pos, Entity entity, float fallDistance) {
        return callOnBlockState(level, pos, Half.POSITIVE, state -> {
            state.getBlock().fallOn(level, state, pos, entity, fallDistance);
            return true;
        }).orElse(false);
    }

    public static boolean updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        final var pos = entity.blockPosition().below();

        if (!blockGetter.getBlockState(pos).is(DSBlocks.MIXED_SLABS)) {
            return false;
        }

        return callOnBlockState(blockGetter, pos, Half.POSITIVE, state -> {
            state.getBlock().updateEntityAfterFallOn(blockGetter, entity);
            return true;
        }).orElse(false);
    }

    public static boolean stepOn(Level level, BlockPos pos, Entity entity) {
        return callOnBlockState(level, pos, Half.POSITIVE, state -> {
            state.getBlock().stepOn(level, pos, state, entity);
            return true;
        }).orElse(false);
    }

    public static Optional<VoxelShape> getCollisionShape(BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return reduceOnBlockStates(blockGetter, pos, state -> state.getCollisionShape(blockGetter, pos, context), Shapes::or);
    }

}
