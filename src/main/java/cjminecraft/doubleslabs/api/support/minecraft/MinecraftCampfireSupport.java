package cjminecraft.doubleslabs.api.support.minecraft;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.block.Block;
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
import net.minecraftforge.registries.ObjectHolder;

import java.util.Random;

@SlabSupportProvider
public class MinecraftCampfireSupport implements IHorizontalSlabSupport {

    @ObjectHolder("extendednether:soul_campfire")
    public static final Block SOUL_CAMPFIRE = null;

    @Override
    public boolean isHorizontalSlab(Block block) {
        return block instanceof CampfireBlock;
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CampfireBlock;
    }

    @Override
    public SlabType getHalf(World world, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof RaisedCampfireBlock ? SlabType.TOP : SlabType.BOTTOM;
    }

    @Override
    public BlockState getStateFromStack(ItemStack stack, BlockItemUseContext context) {
        return IHorizontalSlabSupport.super.getStateFromStack(stack, context);
    }

    private Block getRaisedBlock(Block block) {
        if (block == Blocks.CAMPFIRE)
            return DSBlocks.RAISED_CAMPFIRE.get();
        if (block == SOUL_CAMPFIRE)
            return DSBlocks.RAISED_SOUL_CAMPFIRE.orElseGet(DSBlocks.RAISED_CAMPFIRE);
        return DSBlocks.RAISED_CAMPFIRE.get();
    }


    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, BlockState state, SlabType half) {
        if (half == SlabType.TOP)
            return getRaisedBlock(state.getBlock()).getDefaultState()
                    .with(CampfireBlock.FACING, state.get(CampfireBlock.FACING))
                    .with(CampfireBlock.LIT, state.get(CampfireBlock.LIT))
                    .with(CampfireBlock.SIGNAL_FIRE, state.get(CampfireBlock.SIGNAL_FIRE))
                    .with(CampfireBlock.WATERLOGGED, state.get(CampfireBlock.WATERLOGGED));
        return state;
    }

    @Override
    public boolean areSame(World world, BlockPos pos, BlockState state, ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() == state.getBlock();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType result = state.onBlockActivated(world, player, hand, hit);
        if (!result.isSuccessOrConsume()) {
            if (state.get(CampfireBlock.LIT) && player.getHeldItem(hand).getItem() instanceof ShovelItem) {
                if (!world.isRemote()) {
                    world.playEvent(null, 1009, pos, 0);
                }

                BlockState newState = state.with(CampfireBlock.LIT, Boolean.valueOf(false));

                if (!world.isRemote) {
                    world.setBlockState(pos, newState, Constants.BlockFlags.DEFAULT);
                    player.getHeldItem(hand).damageItem(1, player, (p) -> {
                        p.sendBreakAnimation(hand);
                    });
                }

                return ActionResultType.SUCCESS;
            } else if (player.getHeldItem(hand).getItem() instanceof FlintAndSteelItem && FlintAndSteelItem.isUnlitCampfire(state)) {
                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, new Random().nextFloat() * 0.4F + 0.8F);
                world.setBlockState(pos, state.with(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
                player.getHeldItem(hand).damageItem(1, player, (p) -> {
                    p.sendBreakAnimation(hand);
                });

                return ActionResultType.SUCCESS;
            }
        }
        return result;
    }

    @Override
    public boolean useDoubleSlabModel(BlockState state) {
        return false;
    }

    @Override
    public boolean waterloggableWhenDouble(World world, BlockPos pos, BlockState state) {
        return true;
    }
}
