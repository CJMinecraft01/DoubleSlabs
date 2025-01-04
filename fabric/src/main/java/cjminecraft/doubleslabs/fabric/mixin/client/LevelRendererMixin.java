package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.client.hooks.ClientRenderingHooks;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void renderHitOutline$doubleslabs(PoseStack poseStack, VertexConsumer consumer, Entity entity, double camX, double camY, double camZ, BlockPos pos, BlockState state, int colour, CallbackInfo ci) {
        if (state.is(DSFabricBlocks.MIXED_SLAB)) {
            if (ClientRenderingHooks.renderBlockHighlight(poseStack, camX, camY, camZ, () -> consumer)) {
                ci.cancel();
            }
        }
    }

}
