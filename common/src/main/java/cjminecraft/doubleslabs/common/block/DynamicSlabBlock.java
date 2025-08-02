package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.common.hooks.DynamicSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class DynamicSlabBlock extends BaseEntityBlock {
    public static final MapCodec<DynamicSlabBlock> CODEC = simpleCodec(DynamicSlabBlock::new);

    // Anything common to horizontal and vertical dynamic slabs should go here

    public DynamicSlabBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return DSBlockEntities.DYNAMIC_SLAB.get().create(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        DynamicSlabBlockHooks.randomTick(level, pos, random);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        DynamicSlabBlockHooks.neighborChanged(level, pos, neighborBlock, movedByPiston);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        DynamicSlabBlockHooks.tick(level, pos, random);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        DynamicSlabBlockHooks.entityInside(level, pos, entity);
    }
}
