package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;

public class RaisedCampfireTileEntityRenderer implements BlockEntityRenderer<RaisedCampfireTileEntity> {

    private final CampfireRenderer renderer;

    public RaisedCampfireTileEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.renderer = new CampfireRenderer(context);
    }

    @Override
    public void render(RaisedCampfireTileEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);
        this.renderer.render(tile, partialTicks, poseStack, buffer, combinedLightIn, combinedOverlayIn);
        poseStack.popPose();
    }
}
