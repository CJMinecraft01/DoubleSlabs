package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class DynamicSlabBlock<E extends SlabBlockEntity<?>> extends BaseEntityBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DynamicSlabBlock() {
        super(Properties.of(Material.STONE).noCollission());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @SuppressWarnings("unchecked")
    public Optional<E> getTile(BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile instanceof SlabBlockEntity<?> ? Optional.of((E) tile) : Optional.empty();
    }

    public Optional<IBlockInfo> getAvailable(BlockGetter world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> Optional.of(tile.getPositiveBlockInfo().getBlockState() != null ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo()));
    }

    public int min(BlockGetter world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0);
    }

    public float minFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public int max(BlockGetter world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : 0)).orElse(0);
    }

    public float maxFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public float addFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0) + (tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public void runIfAvailable(BlockGetter world, BlockPos pos, Consumer<IBlockInfo> consumer) {
        getTile(world, pos).map(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                consumer.accept(tile.getPositiveBlockInfo());
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                consumer.accept(tile.getNegativeBlockInfo());
            return null;
        });
    }

    public boolean both(BlockGetter world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null && tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo()) && predicate.test(tile.getNegativeBlockInfo())).orElse(false);
    }

    public boolean either(BlockGetter world, BlockPos pos, Predicate<IBlockInfo> predicate) {
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
