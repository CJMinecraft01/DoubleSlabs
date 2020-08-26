package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.CampfireTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RaisedCampfireTileEntityRenderer extends TileEntityRenderer<RaisedCampfireTileEntity> {

    private final CampfireTileEntityRenderer renderer;

    public RaisedCampfireTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.renderer = new CampfireTileEntityRenderer(rendererDispatcherIn);
    }

    @Override
    public void render(RaisedCampfireTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.push();
        matrixStack.translate(0, 0.5d, 0);
        this.renderer.render(tile, partialTicks, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
        matrixStack.pop();
    }
}
