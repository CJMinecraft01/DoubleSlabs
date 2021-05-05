package cjminecraft.doubleslabs.api.support.minecraft;

import cjminecraft.doubleslabs.api.Flags;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Optional;
import java.util.Random;

@SlabSupportProvider
public class MinecraftCampfireSupport implements IHorizontalSlabSupport {
    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof CampfireBlock;
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

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, BlockState state, SlabType half) {
        if (half == SlabType.TOP)
            return (state.getBlock() == Blocks.CAMPFIRE ? DSBlocks.RAISED_CAMPFIRE.get() : DSBlocks.RAISED_SOUL_CAMPFIRE.get()).getDefaultState()
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
        ActionResultType result = ActionResultType.PASS;

        // the tile at current pos is not a campfire but a slab block and so won't work..

        TileEntity tileentity = Flags.getTileEntityAtPos(pos, world);
        if (tileentity instanceof CampfireTileEntity) {
            CampfireTileEntity campfiretileentity = (CampfireTileEntity)tileentity;
            ItemStack itemstack = player.getHeldItem(hand);
            Optional<CampfireCookingRecipe> optional = campfiretileentity.findMatchingRecipe(itemstack);
            if (optional.isPresent()) {
                if (!world.isRemote && campfiretileentity.addItem(player.abilities.isCreativeMode ? itemstack.copy() : itemstack, optional.get().getCookTime())) {
                    player.addStat(Stats.INTERACT_WITH_CAMPFIRE);
                    result = ActionResultType.SUCCESS;
                }

                result = ActionResultType.CONSUME;
            }
        }
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

    @Override
    public boolean useDoubleSlabModel(BlockState state) {
        return false;
    }

    @Override
    public boolean waterloggableWhenDouble(World world, BlockPos pos, BlockState state) {
        return true;
    }
}
