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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class DynamicSlabBlock extends BaseEntityBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DynamicSlabBlock() {
        super(Properties.of(Material.STONE).noCollission());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean crack(ClientLevel level, BlockState actualState, BlockState state, BlockPos pos, Direction direction, ParticleEngine manager) {
        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            float f = 0.1F;
            AABB aabb = actualState.getShape(level, pos).bounds();
            double d0 = (double)i + level.random.nextDouble() * (aabb.maxX - aabb.minX - (double)0.2F) + (double)0.1F + aabb.minX;
            double d1 = (double)j + level.random.nextDouble() * (aabb.maxY - aabb.minY - (double)0.2F) + (double)0.1F + aabb.minY;
            double d2 = (double)k + level.random.nextDouble() * (aabb.maxZ - aabb.minZ - (double)0.2F) + (double)0.1F + aabb.minZ;
            if (direction == Direction.DOWN) {
                d1 = (double)j + aabb.minY - (double)0.1F;
            }

            if (direction == Direction.UP) {
                d1 = (double)j + aabb.maxY + (double)0.1F;
            }

            if (direction == Direction.NORTH) {
                d2 = (double)k + aabb.minZ - (double)0.1F;
            }

            if (direction == Direction.SOUTH) {
                d2 = (double)k + aabb.maxZ + (double)0.1F;
            }

            if (direction == Direction.WEST) {
                d0 = (double)i + aabb.minX - (double)0.1F;
            }

            if (direction == Direction.EAST) {
                d0 = (double)i + aabb.maxX + (double)0.1F;
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

    public static Optional<SlabBlockEntity<?>> getTile(BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile instanceof SlabBlockEntity<?> ? Optional.of((SlabBlockEntity<?>) tile) : Optional.empty();
    }

    public static Optional<IBlockInfo> getAvailable(BlockGetter world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> Optional.of(tile.getPositiveBlockInfo().getBlockState() != null ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo()));
    }

    public static int min(BlockGetter world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(BlockGetter world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : 0)).orElse(0);
    }

    public static float maxFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static float addFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0) + (tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(BlockGetter world, BlockPos pos, Consumer<IBlockInfo> consumer) {
        getTile(world, pos).map(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                consumer.accept(tile.getPositiveBlockInfo());
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                consumer.accept(tile.getNegativeBlockInfo());
            return null;
        });
    }

    public static boolean both(BlockGetter world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null && tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo()) && predicate.test(tile.getNegativeBlockInfo())).orElse(false);
    }

    public static boolean either(BlockGetter world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo())) || (tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getNegativeBlockInfo()))).orElse(false);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return Services.PLATFORM.getBlockEntities().createSlabBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, Services.PLATFORM.getBlockEntities().dynamicSlab(), SlabBlockEntity::tick);
    }
}
