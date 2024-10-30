package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.common.platform.Services;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

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
}
