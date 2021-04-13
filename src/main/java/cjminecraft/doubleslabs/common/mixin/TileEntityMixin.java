package cjminecraft.doubleslabs.common.mixin;

import cjminecraft.doubleslabs.api.Flags;
import cjminecraft.doubleslabs.api.capability.blockhalf.BlockHalfCapability;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntity.class)
public abstract class TileEntityMixin implements ICapabilityProvider {

    @Shadow
    private World world;
    @Shadow
    private BlockPos pos;
    @Shadow
    private BlockState cachedBlockState;

    private void updateCachedBlockState() {
        TileEntity tile = this.world.getTileEntity(this.pos);
        if (tile instanceof SlabTileEntity) {
            SlabTileEntity slab = (SlabTileEntity) tile;
            this.cachedBlockState = getCapability(BlockHalfCapability.BLOCK_HALF).map(half -> {
                BlockState state = half.isPositiveHalf() ? slab.getPositiveBlockInfo().getBlockState()
                        : slab.getNegativeBlockInfo().getBlockState();
                if (state == null)
                    return world.getBlockState(pos);
                return state;
            }).orElseGet(() -> world.getBlockState(pos));
        }
    }

    @Inject(method = "getBlockState()Lnet/minecraft/block/BlockState;", at = @At("HEAD"), cancellable = true)
    private void getBlockState(CallbackInfoReturnable<BlockState> info) {
        if (this.cachedBlockState == null || this.cachedBlockState.getBlock().equals(DSBlocks.DOUBLE_SLAB) || this.cachedBlockState.getBlock().equals(DSBlocks.VERTICAL_SLAB))
            updateCachedBlockState();
        info.setReturnValue(this.cachedBlockState);
    }

    @Inject(method = "markDirty()V", at = @At("TAIL"))
    private void markDirty(CallbackInfo info) {
        if (this.world != null)
            updateCachedBlockState();
    }

}
