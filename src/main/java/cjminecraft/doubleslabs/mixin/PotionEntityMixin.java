package cjminecraft.doubleslabs.mixin;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class PotionEntityMixin {

    public static void extinguishFires(PotionEntity entity, BlockPos pos, Direction direction) {
        BlockState state = entity.world.getBlockState(pos);
        if (state.getBlock() instanceof DynamicSlabBlock) {
            TileEntity tile = entity.world.getTileEntity(pos);
            if (tile instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) tile;
                extinguishFires(entity, pos, direction, slab.getPositiveBlockInfo());
                extinguishFires(entity, pos, direction, slab.getNegativeBlockInfo());
            }
        }
    }

    private static void extinguishFires(PotionEntity entity, BlockPos pos, Direction direction, IBlockInfo blockInfo) {
        BlockState blockstate = blockInfo.getBlockState();
        if (blockstate == null)
            return;
        Block block = blockstate.getBlock();
        if (block == Blocks.FIRE) {
            entity.world.extinguishFire(null, pos.offset(direction), direction.getOpposite());
        } else if (block == Blocks.CAMPFIRE && blockstate.get(CampfireBlock.LIT)) {
            entity.world.playEvent(null, 1009, pos, 0);
            entity.world.setBlockState(pos, blockstate.with(CampfireBlock.LIT, Boolean.FALSE));
        }
    }
}
