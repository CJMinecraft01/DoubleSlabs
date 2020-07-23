package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TileEntityRendererDoubleSlab extends TileEntitySpecialRenderer<TileEntityDoubleSlab> {

    @Override
    public void render(TileEntityDoubleSlab te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getPositiveTile() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0.5d, 0);
            te.getPositiveTile().setWorld(te.getPositiveWorld());
            te.getPositiveTile().setPos(te.getPos());
            TileEntityRendererDispatcher.instance.render(te.getPositiveTile(), x, y, z, partialTicks, destroyStage, alpha);
            GlStateManager.popMatrix();
        }
        if (te.getNegativeTile() != null) {
            te.getNegativeTile().setWorld(te.getNegativeWorld());
            te.getNegativeTile().setPos(te.getPos());
            TileEntityRendererDispatcher.instance.render(te.getNegativeTile(), x, y, z, partialTicks, destroyStage, alpha);
        }
    }
}
