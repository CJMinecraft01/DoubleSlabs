package cjminecraft.doubleslabs.library.plugins.vanilla.helpers;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.state.Half;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

public class MinecraftSlabHelper implements IHorizontalSlabHelper {

    @Override
    public boolean isHorizontalSlab(Block block) {
        return isHorizontalSlab(block.defaultBlockState());
    }

    @Override
    public boolean isHorizontalSlab(BlockState state) {
        return state.hasProperty(BlockStateProperties.SLAB_TYPE);
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return item instanceof BlockItem blockItem && isHorizontalSlab(blockItem.getBlock().defaultBlockState());
    }

    @Override
    public Half getHalf(BlockGetter level, BlockPos pos, BlockState state) {
        return switch (state.getValue(BlockStateProperties.SLAB_TYPE)) {
            case BOTTOM -> Half.BOTTOM;
            case TOP -> Half.TOP;
            case DOUBLE -> throw new IllegalStateException("Cannot get the half for a double slab");
        };
    }

    @Override
    public BlockState getStateForHalf(BlockGetter level, BlockPos pos, BlockState state, Half half) {
        return state.setValue(BlockStateProperties.SLAB_TYPE, half == Half.TOP ? SlabType.TOP : SlabType.BOTTOM);
    }
}
