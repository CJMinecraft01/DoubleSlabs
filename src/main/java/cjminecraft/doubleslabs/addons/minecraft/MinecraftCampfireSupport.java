package cjminecraft.doubleslabs.addons.minecraft;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class MinecraftCampfireSupport implements ISlabSupport {

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof CampfireBlock;
    }

    @Override
    public boolean isHorizontalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof CampfireBlock;
    }

    @Override
    public SlabType getHalf(World world, BlockPos pos, BlockState state) {
        return SlabType.BOTTOM;
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, BlockItemUseContext context, SlabType half) {
        BlockItem slab = (BlockItem) stack.getItem();
        return slab.getBlock().getStateForPlacement(context);
    }

    @Override
    public boolean areSame(World world, BlockPos pos, BlockState state, ItemStack stack) {
        return ((BlockItem) stack.getItem()).getBlock() == state.getBlock();
    }

    @Override
    public float getOffsetY(boolean positive) {
        return positive ? 0.5f : 0;
    }

    @Override
    public ActionResultType onActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType result = state.onBlockActivated(world, player, hand, hit);
        if (!result.isSuccessOrConsume()) {
            if (state.get(CampfireBlock.LIT) && player.getHeldItem(hand).getItem() instanceof ShovelItem) {
                if (!world.isRemote()) {
                    world.playEvent(null, 1009, pos, 0);
                }

                CampfireBlock.extinguish(world, pos, state);
                BlockState newState = state.with(CampfireBlock.LIT, Boolean.valueOf(false));

                if (!world.isRemote) {
                    world.setBlockState(pos, newState, Constants.BlockFlags.DEFAULT);
                    player.getHeldItem(hand).damageItem(1, player, (p) -> {
                        p.sendBreakAnimation(hand);
                    });
                }

                return ActionResultType.func_233537_a_(world.isRemote);
            } else if (player.getHeldItem(hand).getItem() instanceof FlintAndSteelItem && CampfireBlock.canBeLit(state)) {
                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, new Random().nextFloat() * 0.4F + 0.8F);
                world.setBlockState(pos, state.with(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
                player.getHeldItem(hand).damageItem(1, player, (p) -> {
                    p.sendBreakAnimation(hand);
                });

                return ActionResultType.func_233537_a_(world.isRemote);
            }
        }
        return result;
    }
}
