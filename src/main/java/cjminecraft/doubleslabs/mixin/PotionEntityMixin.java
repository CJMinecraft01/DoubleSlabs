package cjminecraft.doubleslabs.mixin;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends Entity {

    public PotionEntityMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(at = @At("TAIL"), method = "extinguishFires(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)V")
    private void extinguishFires(BlockPos pos, Direction direction, CallbackInfo ci) {
        BlockState state = this.world.getBlockState(pos);
        if (state.getBlock() instanceof DynamicSlabBlock) {
            TileEntity tile = this.world.getTileEntity(pos);
            if (tile instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) tile;
                extinguishFires(pos, direction, slab.getPositiveBlockInfo());
                extinguishFires(pos, direction, slab.getNegativeBlockInfo());
            }
        }
    }

    private void extinguishFires(BlockPos pos, Direction direction, IBlockInfo block) {
        BlockState blockstate = block.getBlockState();
        if (blockstate == null)
            return;
        if (blockstate.isIn(BlockTags.FIRE)) {
            this.world.removeBlock(pos, false);
        } else if (CampfireBlock.isLit(blockstate)) {
            this.world.playEvent(null, 1009, pos, 0);
            CampfireBlock.extinguish(block.getWorld(), pos, blockstate);
            block.setBlockState(blockstate.with(CampfireBlock.LIT, Boolean.FALSE));
        }
    }
}
