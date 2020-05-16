package cjminecraft.doubleslabs.addons.minecraft;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MinecraftSlabSupport implements ISlabSupport {

    private boolean isValid(BlockState state) {
        return (state.getBlock() instanceof SlabBlock && state.get(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE && hasEnumHalfProperty(state)) || (hasEnumHalfProperty(state) && state.get(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE);
    }

    private boolean hasEnumHalfProperty(BlockState state) {
        return state.getProperties().contains(BlockStateProperties.SLAB_TYPE);
    }

    @Override
    public boolean isValid(World world, BlockPos pos, BlockState state) {
        return isValid(state);
    }

    @Override
    public boolean isValid(ItemStack stack, PlayerEntity player, Hand hand) {
        return stack.getItem() instanceof BlockItem && isValid(((BlockItem) stack.getItem()).getBlock().getDefaultState());
    }

    @Override
    public SlabType getHalf(World world, BlockPos pos, BlockState state) {
        return state.get(BlockStateProperties.SLAB_TYPE);
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, SlabType half) {
        BlockItem slab = (BlockItem) stack.getItem();
        return slab.getBlock().getDefaultState().with(BlockStateProperties.SLAB_TYPE, half);
    }

    @Override
    public boolean areSame(World world, BlockPos pos, BlockState state, ItemStack stack) {
        return ((BlockItem) stack.getItem()).getBlock() == state.getBlock();
    }
}
