package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.fabric.api.client.IDynamicSlabRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(AbstractBlockRenderContext.class)
public class AbstractBlockRenderContextMixin implements IDynamicSlabRenderContext {

    @Shadow(remap = false) @Final protected BlockRenderInfo blockInfo;

    private final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

    private int cullCompletionFlags;
    private int cullResultFlags;

    @Override
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAo) {
        blockInfo.prepareForBlock(blockState, blockPos, modelAo);

        cullCompletionFlags = 0;
        cullResultFlags = 0;
    }

    @Inject(remap = false, method = "isFaceCulled", at = @At("HEAD"), cancellable = true)
    private void isFaceCulled$doubleslabs(@Nullable Direction face, CallbackInfoReturnable<Boolean> cir) {
        if (face == null) {
            return;
        }

        if (blockInfo.blockView != null && blockInfo.blockPos != null) {
            final var actualState = blockInfo.blockView.getBlockState(blockInfo.blockPos);

            if (!actualState.is(DSBlocks.VERTICAL_SLAB.get())) {
                return;
            }

            // Use the actual vertical slab state for culling
            final var mask = 1 << face.get3DDataValue();

            if ((cullCompletionFlags & mask) == 0) {
                cullCompletionFlags |= mask;

                if (Block.shouldRenderFace(actualState, blockInfo.blockView, blockInfo.blockPos, face, searchPos.setWithOffset(blockInfo.blockPos, face))) {
                    cullResultFlags |= mask;
                }
            }

            cir.setReturnValue((cullResultFlags & mask) == 0);
        }
    }
}
