package cjminecraft.doubleslabs.api.support.minecraft;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SlabSupportProvider
public class MinecraftSlabSupport implements IHorizontalSlabSupport {

    private boolean isValid(BlockState state) {
        return (state.getBlock() instanceof SlabBlock && state.get(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE && hasEnumHalfProperty(state)) || (hasEnumHalfProperty(state) && state.get(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE);
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        return block instanceof SlabBlock;
    }

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return isValid(state);
    }

    private boolean hasEnumHalfProperty(BlockState state) {
        return state.getProperties().contains(BlockStateProperties.SLAB_TYPE);
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return item instanceof BlockItem && ((BlockItem) item).getBlock() != null && isValid(((BlockItem) item).getBlock().getDefaultState());
    }

    @Override
    public SlabType getHalf(IBlockReader world, BlockPos pos, BlockState state) {
        return state.get(BlockStateProperties.SLAB_TYPE);
    }

    @Override
    public BlockState getStateForHalf(BlockState state, SlabType half) {
        return state.with(BlockStateProperties.SLAB_TYPE, half);
    }

    @Override
    public boolean useDoubleSlabModel(BlockState state) {
        return !state.getProperties().stream().anyMatch(property -> property.getValueClass() == Direction.class);
    }
}
