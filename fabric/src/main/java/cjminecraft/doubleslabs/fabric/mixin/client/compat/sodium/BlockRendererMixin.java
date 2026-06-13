package cjminecraft.doubleslabs.fabric.mixin.client.compat.sodium;

import cjminecraft.doubleslabs.common.init.DSBlocks;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {

    @Shadow(remap = false)
    @Final
    private BlockOcclusionCache occlusionCache;

    @Inject(remap = false, method = "isFaceVisible", at = @At("HEAD"), cancellable = true)
    private void isFaceVisible$doubleslabs(BlockRenderContext ctx, Direction face, CallbackInfoReturnable<Boolean> cir) {
        final var actualState = ctx.world().getBlockState(ctx.pos());

        if (!actualState.is(DSBlocks.VERTICAL_SLAB.get())) {
            return;
        }

        cir.setReturnValue(occlusionCache.shouldDrawSide(actualState, ctx.world(), ctx.pos(), face));
    }

}
