package cjminecraft.doubleslabs.old.client.render;

import cjminecraft.doubleslabs.old.tileentitiy.TileEntityDoubleSlab;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class TileEntityRendererDoubleSlab extends TileEntityRenderer<TileEntityDoubleSlab> {
    public TileEntityRendererDoubleSlab(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityDoubleSlab tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if (tile.getPositiveTile() != null) {
            matrixStack.push();
            matrixStack.translate(0, 0.5d, 0);
            TileEntityRendererDispatcher.instance.renderTileEntity(tile.getPositiveTile(), partialTicks, matrixStack, buffer);
            matrixStack.pop();
        }
        if (tile.getNegativeTile() != null)
            TileEntityRendererDispatcher.instance.renderTileEntity(tile.getNegativeTile(), partialTicks, matrixStack, buffer);
    }
}
