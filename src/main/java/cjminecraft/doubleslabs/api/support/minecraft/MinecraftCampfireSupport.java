package cjminecraft.doubleslabs.api.support.minecraft;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

@SlabSupportProvider
public class MinecraftCampfireSupport implements IHorizontalSlabSupport {

    @Override
    public boolean isHorizontalSlab(Block block) {
        return block instanceof CampfireBlock;
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CampfireBlock;
    }

    @Override
    public SlabType getHalf(BlockGetter world, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof RaisedCampfireBlock ? SlabType.TOP : SlabType.BOTTOM;
    }

    private Block getRaisedBlock(Block block) {
        if (block == Blocks.CAMPFIRE)
            return DSBlocks.RAISED_CAMPFIRE.get();
        if (block == Blocks.SOUL_CAMPFIRE)
            return DSBlocks.RAISED_SOUL_CAMPFIRE.get();
        return DSBlocks.RAISED_CAMPFIRE.get();
    }

    @Override
    public BlockState getStateForHalf(BlockGetter world, BlockPos pos, BlockState state, SlabType half) {
        if (half == SlabType.TOP)
            return getRaisedBlock(state.getBlock()).defaultBlockState()
                    .setValue(CampfireBlock.FACING, state.getValue(CampfireBlock.FACING))
                    .setValue(CampfireBlock.LIT, state.getValue(CampfireBlock.LIT))
                    .setValue(CampfireBlock.SIGNAL_FIRE, state.getValue(CampfireBlock.SIGNAL_FIRE))
                    .setValue(CampfireBlock.WATERLOGGED, state.getValue(CampfireBlock.WATERLOGGED));
        return state;
    }

    @Override
    public boolean areSame(Level world, BlockPos pos, BlockState state, ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() == state.getBlock();
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = state.use(world, player, hand, hit);
        if (!result.consumesAction()) {
            if (state.getValue(CampfireBlock.LIT) && player.getItemInHand(hand).getItem() instanceof ShovelItem) {
                if (!world.isClientSide()) {
                    world.levelEvent(null, 1009, pos, 0);
                }

                CampfireBlock.dowse(player, world, pos, state);
                BlockState newState = state.setValue(CampfireBlock.LIT, Boolean.valueOf(false));

                if (!world.isClientSide()) {
                    world.setBlock(pos, newState, Constants.BlockFlags.DEFAULT);
                    player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                }

                return InteractionResult.sidedSuccess(world.isClientSide());
            } else if (player.getItemInHand(hand).getItem() instanceof FlintAndSteelItem && CampfireBlock.canLight(state)) {
                world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, new Random().nextFloat() * 0.4F + 0.8F);
                world.setBlock(pos, state.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
                player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));

                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }
        return result;
    }

    @Override
    public boolean useDoubleSlabModel(BlockState state) {
        return false;
    }

    @Override
    public boolean waterloggableWhenDouble(Level world, BlockPos pos, BlockState state) {
        return true;
    }
}
