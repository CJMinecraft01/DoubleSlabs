package cjminecraft.doubleslabs.library.plugins.vanilla.helpers;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

public class MinecraftSlabHelper implements IHorizontalSlabHelper {

    @Override
    public boolean isHorizontalSlab(Block block) {
        return block instanceof SlabBlock;
    }

    @Override
    public Half getHalf(BlockGetter level, BlockPos pos, BlockState state) {
        return switch (state.getValue(BlockStateProperties.SLAB_TYPE)) {
            case BOTTOM -> Half.BOTTOM;
            case TOP -> Half.TOP;
            case DOUBLE -> throw new RuntimeException("Cannot get the half for a double slab");
        };
    }

    @Override
    public BlockState getStateForHalf(BlockGetter level, BlockPos pos, BlockState state, Half half) {
        return state.setValue(BlockStateProperties.SLAB_TYPE, half == Half.TOP ? SlabType.TOP : SlabType.BOTTOM);
    }
}
