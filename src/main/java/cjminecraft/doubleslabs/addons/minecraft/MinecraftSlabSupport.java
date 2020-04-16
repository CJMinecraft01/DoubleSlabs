package cjminecraft.doubleslabs.addons.minecraft;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MinecraftSlabSupport implements ISlabSupport {

    private boolean isValid(BlockState state) {
        return (state.getBlock() instanceof SlabBlock && state.get(SlabBlock.TYPE) != SlabType.DOUBLE && hasEnumHalfProperty(state)) || hasEnumHalfProperty(state);
    }

    private boolean hasEnumHalfProperty(BlockState state) {
        return state.getProperties().contains(SlabBlock.TYPE);
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
        return state.get(SlabBlock.TYPE);
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, SlabType half) {
        BlockState state = Block.getBlockFromItem(stack.getItem()).getDefaultState();
        return state.with(SlabBlock.TYPE, half);
    }

    @Override
    public boolean areSame(World world, BlockPos pos, BlockState state, ItemStack stack) {
        return ((BlockItem) stack.getItem()).getBlock() == state.getBlock();
    }
}
