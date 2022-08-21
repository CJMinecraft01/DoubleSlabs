package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class DynamicSlabBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DynamicSlabBlock() {
        super(Properties.of(Material.STONE).noCollission());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    public static Optional<SlabBlockEntity<?>> getSlab(BlockGetter level, BlockPos pos) {
        BlockEntity slab = level.getBlockEntity(pos);
        return slab instanceof SlabBlockEntity<?> ? Optional.of((SlabBlockEntity<?>) slab) : Optional.empty();
    }

    public static Optional<IBlockInfo> getAvailable(BlockGetter level, BlockPos pos) {
        return getSlab(level, pos).flatMap(slab -> Optional.of(slab.getPositiveBlockInfo().getBlockState() != null ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo()));
    }

    public static int min(BlockGetter level, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getSlab(level, pos).map(slab -> Math.min(slab.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(slab.getPositiveBlockInfo()) : Integer.MAX_VALUE, slab.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(slab.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(BlockGetter level, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getSlab(level, pos).map(slab -> Math.min(slab.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(slab.getPositiveBlockInfo()) : Integer.MAX_VALUE, slab.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(slab.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(BlockGetter level, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getSlab(level, pos).map(slab -> Math.max(slab.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(slab.getPositiveBlockInfo()) : 0, slab.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(slab.getNegativeBlockInfo()) : 0)).orElse(0);
    }

    public static float maxFloat(BlockGetter level, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getSlab(level, pos).map(slab -> Math.max(slab.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(slab.getPositiveBlockInfo()) : 0, slab.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(slab.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static float addFloat(BlockGetter level, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getSlab(level, pos).map(slab -> (slab.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(slab.getPositiveBlockInfo()) : 0) + (slab.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(slab.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(BlockGetter level, BlockPos pos, Consumer<IBlockInfo> consumer) {
        getSlab(level, pos).map(slab -> {
            if (slab.getPositiveBlockInfo().getBlockState() != null)
                consumer.accept(slab.getPositiveBlockInfo());
            if (slab.getNegativeBlockInfo().getBlockState() != null)
                consumer.accept(slab.getNegativeBlockInfo());
            return null;
        });
    }

    public static boolean both(BlockGetter level, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getSlab(level, pos).map(slab -> slab.getPositiveBlockInfo().getBlockState() != null && slab.getNegativeBlockInfo().getBlockState() != null && predicate.test(slab.getPositiveBlockInfo()) && predicate.test(slab.getNegativeBlockInfo())).orElse(false);
    }

    public static boolean either(BlockGetter level, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getSlab(level, pos).map(slab -> (slab.getPositiveBlockInfo().getBlockState() != null && predicate.test(slab.getPositiveBlockInfo())) || (slab.getNegativeBlockInfo().getBlockState() != null && predicate.test(slab.getNegativeBlockInfo()))).orElse(false);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean crack(ClientLevel level, BlockState actualState, BlockState state, BlockPos pos, Direction direction, ParticleEngine manager) {
        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            AABB aabb = actualState.getShape(level, pos).bounds();
            double d0 = (double) i + level.random.nextDouble() * (aabb.maxX - aabb.minX - (double) 0.2F) + (double) 0.1F + aabb.minX;
            double d1 = (double) j + level.random.nextDouble() * (aabb.maxY - aabb.minY - (double) 0.2F) + (double) 0.1F + aabb.minY;
            double d2 = (double) k + level.random.nextDouble() * (aabb.maxZ - aabb.minZ - (double) 0.2F) + (double) 0.1F + aabb.minZ;
            if (direction == Direction.DOWN) {
                d1 = (double) j + aabb.minY - (double) 0.1F;
            }

            if (direction == Direction.UP) {
                d1 = (double) j + aabb.maxY + (double) 0.1F;
            }

            if (direction == Direction.NORTH) {
                d2 = (double) k + aabb.minZ - (double) 0.1F;
            }

            if (direction == Direction.SOUTH) {
                d2 = (double) k + aabb.maxZ + (double) 0.1F;
            }

            if (direction == Direction.WEST) {
                d0 = (double) i + aabb.minX - (double) 0.1F;
            }

            if (direction == Direction.EAST) {
                d0 = (double) i + aabb.maxX + (double) 0.1F;
            }

            Particle particle = manager.createParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            if (particle != null) {
                particle = particle.setPower(0.2F).scale(0.6F);
                manager.add(particle);
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return Services.REGISTRIES.getBlockEntities().createSlabBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, Services.REGISTRIES.getBlockEntities().dynamicSlab(), SlabBlockEntity::tick);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluidState) {
        runIfAvailable(level, pos, i -> i.blockState()
                .filter(s -> s.getBlock() instanceof SimpleWaterloggedBlock)
                .ifPresent(s -> ((SimpleWaterloggedBlock) s.getBlock())
                        .placeLiquid(i.getLevel(), pos, s, fluidState)));
        return SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    @Override
    public boolean canPlaceLiquid(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Fluid fluid) {
        return SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid) &&
                either(level, pos, i -> i.support().map(s -> s.waterloggableWhenDouble(i.getLevel(), pos, i.getBlockState())).orElse(false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        FluidState fluidState = context.getLevel().getFluidState(pos);
        if (state.is(this)) {
            return state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER &&
                    either(context.getLevel(), pos, i -> i.support()
                            .map(s -> s.waterloggableWhenDouble(i.getLevel(), i.getPos(), i.getBlockState()))
                            .orElse(false)));
        } else {
            return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull PathComputationType type) {
        return type == PathComputationType.WATER && level.getFluidState(pos).is(FluidTags.WATER);
    }

    // todo: explosion resistance


    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return max(level, pos, i -> i.blockState().map(s -> s.getLightBlock(i.getLevel(), pos))
                .orElseGet(() -> super.getLightBlock(state, level, pos)));
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1F;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        List<ItemStack> drops = new ArrayList<>();

        if (blockEntity instanceof SlabBlockEntity<?> slab) {
            slab.getPositiveBlockInfo().blockState().ifPresent(s -> {
                LootContext.Builder newBuilder = builder.withParameter(LootContextParams.BLOCK_STATE, s);
                slab.getPositiveBlockInfo().blockEntity()
                        .ifPresent(e -> newBuilder.withParameter(LootContextParams.BLOCK_ENTITY, e));
                drops.addAll(s.getDrops(newBuilder));
            });
            slab.getNegativeBlockInfo().blockState().ifPresent(s -> {
                LootContext.Builder newBuilder = builder.withParameter(LootContextParams.BLOCK_STATE, s);
                slab.getNegativeBlockInfo().blockEntity()
                        .ifPresent(e -> newBuilder.withParameter(LootContextParams.BLOCK_ENTITY, e));
                drops.addAll(s.getDrops(newBuilder));
            });
        }
        return drops;
    }

    // todo: on destroyed by player


    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, Player player) {
        if (player.isCreative()) {
            runIfAvailable(level, pos, i -> i.blockState().ifPresent(s -> s.onRemove(i.getLevel(), pos, Blocks.AIR.defaultBlockState(), false)));
            super.playerWillDestroy(level, pos, state, player); // todo: move outside of if?
        }
    }

    // todo: block color

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.randomTick(state, level, pos, random);
        runIfAvailable(level, pos, i -> i.blockState().filter(BlockStateBase::isRandomlyTicking)
                .ifPresent(s -> s.randomTick((ServerLevel) i.getLevel(), pos, random)));
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        runIfAvailable(level, pos, i -> i.blockState().ifPresent(s -> s.getBlock().animateTick(s, i.getLevel(), pos, random)));
    }

    // todo: get enchant power bonus
    // todo: get fire spread speed
    // todo: get flammability
    // todo: get weak changes
    // todo: should check weak power
    // todo: is burning
    // todo: is fertile
    // todo: is fire source
    // todo: is flammable


    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        runIfAvailable(level, pos, i -> i.blockState().ifPresent(s -> s.neighborChanged(i.getLevel(), pos, s.getBlock(), fromPos, isMoving)));
    }

    // todo: on neighbor change


    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        runIfAvailable(level, pos, i -> i.blockState().ifPresent(s -> s.tick((ServerLevel) i.getLevel(), pos, random)));
    }

    @Override
    public void wasExploded(@NotNull Level level, @NotNull BlockPos pos, @NotNull Explosion explosion) {
        runIfAvailable(level, pos, i -> i.blockState().ifPresent(s -> s.getBlock().wasExploded(i.getLevel(), pos, explosion)));
    }

    @Override
    public void handlePrecipitation(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Biome.@NotNull Precipitation precipitation) {
        getSlab(level, pos).ifPresent(slab -> slab.getPositiveBlockInfo().blockState()
                .ifPresent(s -> s.getBlock().handlePrecipitation(s, slab.getPositiveBlockInfo().getLevel(), pos, precipitation)));
    }

    // todo: get friction
    // todo: can sustain plant


    @Override
    public void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, int flags, int recursionLeft) {
        runIfAvailable(level, pos, i -> i.blockState().ifPresent(s -> s.updateIndirectNeighbourShapes(i.getLevel(), pos, flags, recursionLeft)));
    }

    // todo: is ladder
    // todo: is bed
    // todo: is conduit frame
    // todo: is portal frame
    // todo: get exp drop
    // todo: get sound type
    // todo: get beacon color multiplier
    // todo: get block path type
    // todo: on caught fire
    // todo: can entity destroy
    // todo: on block exploded


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }
}
