package cjminecraft.doubleslabs.api.support.minecraft;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

@SlabSupportProvider
public class MinecraftSlabSupport implements IHorizontalSlabSupport {

    private boolean isValid(BlockState state) {
        return (state.getBlock() instanceof SlabBlock && state.getValue(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE && hasEnumHalfProperty(state)) || (hasEnumHalfProperty(state) && state.getValue(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE);
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        return block instanceof SlabBlock;
    }

    @Override
    public boolean isHorizontalSlab(BlockGetter world, BlockPos pos, BlockState state) {
        return isValid(state);
    }

    private boolean hasEnumHalfProperty(BlockState state) {
        return state.getProperties().contains(BlockStateProperties.SLAB_TYPE);
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return item instanceof BlockItem && ((BlockItem) item).getBlock() != null && isValid(((BlockItem) item).getBlock().defaultBlockState());
    }

    @Override
    public SlabType getHalf(BlockGetter world, BlockPos pos, BlockState state) {
        return state.getValue(BlockStateProperties.SLAB_TYPE);
    }

    @Override
    public BlockState getStateForHalf(BlockGetter world, BlockPos pos, BlockState state, SlabType half) {
        return state.setValue(BlockStateProperties.SLAB_TYPE, half);
    }

    @Override
    public boolean useDoubleSlabModel(BlockState state) {
        return !state.getProperties().stream().anyMatch(property -> property.getValueClass() == Direction.class);
    }
}
