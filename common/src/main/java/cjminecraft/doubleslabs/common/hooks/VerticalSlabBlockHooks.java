package cjminecraft.doubleslabs.common.hooks;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    protected static Half getHalfFromPlayerUsingCollision(final Player player, final VoxelShape collisionShape, final BlockState verticalSlabState, final BlockPos slabPos) {
        final var clipStart = player.getEyePosition();
        final var clipEnd = player.getEyePosition().add(player.getLookAngle().scale(player.blockInteractionRange()));

        final var hitResult = Objects.requireNonNull(collisionShape.clip(clipStart, clipEnd, slabPos));

        return Objects.requireNonNull(getHalfFromHitResult(verticalSlabState, hitResult, slabPos));
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

    protected static <T> Optional<T> callOnBlockStateBelow(final BlockGetter blockGetter, final BlockState state, final BlockPos pos, final Entity entity, final Function<BlockState, T> function) {
        final var type = state.getValue(VerticalSlabBlock.TYPE);

        if (type != VerticalSlabType.DOUBLE) {
            return callOnBlockState(blockGetter, pos, type.getHalf(), function);
        }

        final @Nullable Half slabHalf = getHalfFromHitResult(state, new BlockHitResult(entity.position().subtract(0, 1E-5F, 0), Direction.UP, pos, true), pos);

        if (slabHalf == null) {
            return Optional.empty();
        }

        return callOnBlockState(blockGetter, pos, slabHalf, function);
    }

    public static Optional<Float> getDestroyProgress(Player player, BlockGetter blockGetter, BlockState state, BlockPos pos) {
        return callOnLookingAtBlockState(blockGetter, state, pos, player, s -> s.getDestroyProgress(player, blockGetter, pos)).or(() -> minFromBlockState(blockGetter, pos, s -> s.getDestroyProgress(player, blockGetter, pos)));
    }

    public static boolean removeBlock(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest) {
        // If we will harvest the block then destroy the block using player destroy
        if (willHarvest) {
            return true;
        }

        final var type = state.getValue(VerticalSlabBlock.TYPE);

        // If the player is crouching in creative then break the slabs separately
        if (player.isCreative() && player.isCrouching() && type == VerticalSlabType.DOUBLE) {
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

        final var type = state.getValue(VerticalSlabBlock.TYPE);

        if (!(blockEntity instanceof IDynamicSlabStateContainer container) || type != VerticalSlabType.DOUBLE) {
            player.causeFoodExhaustion(0.005F);
            Block.dropResources(state, level, pos, blockEntity, player, tool);
            level.removeBlock(pos, false);
        } else {
            final var halfToRemove = getHalfFromPlayerUsingCollision(player, state.getCollisionShape(level, pos), state, pos);

            destroyHalf(container, player, level, pos, tool, halfToRemove, slabContainer -> {
                if (level instanceof ServerLevel) {
                    final var slabState = slabContainer.getBlockState();
                    final var slabItem = slabState.getBlock().asItem();
                    Block.getDrops(slabState, (ServerLevel)level, pos, slabContainer.getBlockEntity(), player, tool).stream()
                            .map(stack -> stack.is(slabItem) ? VerticalSlabItem.of(stack) : stack)
                            .forEach((stack) -> Block.popResource(level, pos, stack));
                    state.spawnAfterBreak((ServerLevel)level, pos, tool, true);
                }
            });

            final var halfToKeep = halfToRemove.getOpposite();

            container.clearStateContainer(halfToRemove);
            level.setBlock(pos, state.setValue(VerticalSlabBlock.TYPE, VerticalSlabType.fromHalf(halfToKeep)), 3);
        }
    }

    public static ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, HitResult hitResult) {
        return callOnLookingAtBlockState(level, state, pos, hitResult, s -> {
            final var slab = s.getBlock().getCloneItemStack(level, pos, s);

            return VerticalSlabItem.of(slab);
        }).orElse(ItemStack.EMPTY);
    }

    public static List<ItemStack> getDrops(LootParams.Builder params) {
        final var drops = new ArrayList<ItemStack>();

        final var blockEntity = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof DynamicSlabBlockEntity<?> dynamicSlab) {
            dynamicSlab.runOnStateContainers(container -> {
                if (!container.hasBlockState()) {
                    return;
                }

                final var slabState = container.getBlockState();

                var slabParams = params.withParameter(LootContextParams.BLOCK_STATE, slabState);

                if (container.hasBlockEntity()) {
                    slabParams = slabParams.withParameter(LootContextParams.BLOCK_ENTITY, Objects.requireNonNull(container.getBlockEntity()));
                }

                final var slabItem = slabState.getBlock().asItem();
                final var slabDrops = slabState.getDrops(slabParams).stream()
                        .map(stack -> stack.is(slabItem) ? VerticalSlabItem.of(stack) : stack)
                        .toList();

                drops.addAll(slabDrops);
            });
        }

        return drops;
    }

    public static Optional<SoundType> getSoundType(BlockGetter blockGetter, BlockState verticalSlabState, BlockPos pos, @Nullable Entity entity) {
        final var type = verticalSlabState.getValue(VerticalSlabBlock.TYPE);

        // Single vertical slabs can just use the sound type for the existing slab
        if (type != VerticalSlabType.DOUBLE) {
            return callOnBlockState(blockGetter, pos, type.getHalf(), BlockBehaviour.BlockStateBase::getSoundType);
        }

        if (entity instanceof Player player) {
            // We first assume that we are destroying a block and so get the state based on what the player is looking at
            final var destroyBlockSound = callOnLookingAtBlockState(blockGetter, verticalSlabState, pos, player, BlockBehaviour.BlockStateBase::getSoundType);
            if (destroyBlockSound.isPresent()) {
                return destroyBlockSound;
            }
            // If the player is not looking at this slab block then treat it like all other entities
        }

        // If we have an entity, get the sound type for the slab they are on
        if (entity != null) {
            return callOnBlockStateBelow(blockGetter, verticalSlabState, pos, entity, BlockBehaviour.BlockStateBase::getSoundType);
        }

        return Optional.empty();
    }

    public static Optional<BlockParticleOption> getParticleForLanding(BlockGetter blockGetter, BlockState verticalSlabState, BlockPos pos, Entity entity) {
        return callOnBlockStateBelow(blockGetter, verticalSlabState, pos, entity, state -> new BlockParticleOption(ParticleTypes.BLOCK, state));
    }

    public static boolean propagateSkylightDown(BlockGetter blockGetter, BlockPos pos) {
        return requireEitherStates(blockGetter, pos, state -> state.propagatesSkylightDown(blockGetter, pos));
    }

    public static boolean fallOn(Level level, BlockState verticalSlabState, BlockPos pos, Entity entity, float fallDistance) {
        return callOnBlockStateBelow(level, verticalSlabState, pos, entity, state -> {
            state.getBlock().fallOn(level, state, pos, entity, fallDistance);
            return true;
        }).orElse(false);
    }

    public static boolean updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        final var pos = entity.getOnPos();
        final var verticalSlabState = blockGetter.getBlockState(pos);

        if (!verticalSlabState.is(DSBlocks.VERTICAL_SLAB.get())) {
            return false;
        }

        return callOnBlockStateBelow(blockGetter, verticalSlabState, pos, entity, state -> {
            state.getBlock().updateEntityAfterFallOn(blockGetter, entity);
            return true;
        }).orElse(false);
    }

    public static boolean stepOn(Level level, BlockState verticalSlabState, BlockPos pos, Entity entity) {
        return callOnBlockStateBelow(level, verticalSlabState, pos, entity, state -> {
            state.getBlock().stepOn(level, pos, state, entity);
            return true;
        }).orElse(false);
    }

}
