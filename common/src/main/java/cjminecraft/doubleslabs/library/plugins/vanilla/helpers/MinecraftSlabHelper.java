package cjminecraft.doubleslabs.library.plugins.vanilla.helpers;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.state.Half;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
        //noinspection ConstantValue - Some mods have set their block for a block item to be null
        return item instanceof BlockItem blockItem &&
                blockItem.getBlock() != null &&
                isHorizontalSlab(blockItem.getBlock().defaultBlockState());
    }

    @Override
    public Half getHalf(BlockState state) {
        return switch (state.getValue(BlockStateProperties.SLAB_TYPE)) {
            case BOTTOM -> Half.NEGATIVE;
            case TOP -> Half.POSITIVE;
            case DOUBLE -> throw new IllegalStateException("Cannot get the half for a double slab");
        };
    }

    @Override
    public boolean isDoubleSlab(BlockState state) {
        return state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE;
    }

    @Override
    public BlockState getStateForHalf(BlockState state, Half half) {
        return state.setValue(BlockStateProperties.SLAB_TYPE, half == Half.POSITIVE ? SlabType.TOP : SlabType.BOTTOM);
    }
}
