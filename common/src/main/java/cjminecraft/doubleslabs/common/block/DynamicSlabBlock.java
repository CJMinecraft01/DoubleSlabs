package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.common.hooks.DynamicSlabHooks;
import cjminecraft.doubleslabs.common.platform.Services;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import javax.annotation.Nullable;
import java.util.List;

public class DynamicSlabBlock extends BaseEntityBlock {
    public static final MapCodec<DynamicSlabBlock> CODEC = simpleCodec(DynamicSlabBlock::new);

    // Anything common to horizontal and vertical dynamic slabs should go here

    public DynamicSlabBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return Services.PLATFORM.getBlockEntityFactory().createDynamicSlabBlockEntity(pos, state);
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
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return DynamicSlabHooks.getDrops(params);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        DynamicSlabHooks.randomTick(level, pos, random);
    }
}
