package cjminecraft.doubleslabs.mixin;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ThrownPotionMixin {

    public static void dowseFire(ThrownPotion potion, BlockPos pos) {
        BlockState state = potion.level.getBlockState(pos);
        if (state.getBlock() instanceof DynamicSlabBlock) {
            BlockEntity entity = potion.level.getBlockEntity(pos);
            if (entity instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) entity;
                dowseFire(potion, pos, slab.getPositiveBlockInfo());
                dowseFire(potion, pos, slab.getNegativeBlockInfo());
            }
        }
    }

    private static void dowseFire(ThrownPotion potion, BlockPos pos, IBlockInfo block) {
        BlockState blockstate = block.getBlockState();
        if (blockstate == null)
            return;
        if (blockstate.is(BlockTags.FIRE)) {
            potion.level.removeBlock(pos, false);
        } else if (AbstractCandleBlock.isLit(blockstate)) {
            AbstractCandleBlock.extinguish(null, blockstate, block.getWorld(), pos);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            potion.level.levelEvent(null, 1009, pos, 0);
            CampfireBlock.dowse(potion.getOwner(), block.getWorld(), pos, blockstate);
            block.setBlockState(blockstate.setValue(CampfireBlock.LIT, Boolean.FALSE));
        }
    }
}