package cjminecraft.doubleslabs.old.client.render;

import cjminecraft.doubleslabs.old.tileentitiy.TileEntityDoubleSlab;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class TileEntityRendererDoubleSlab extends TileEntityRenderer<TileEntityDoubleSlab> {

    @Override
    public void render(TileEntityDoubleSlab te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.getPositiveTile() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0, 0.5f, 0);
            te.getPositiveTile().setWorld(te.getPositiveWorld());
            te.getPositiveTile().setPos(te.getPos());
            TileEntityRendererDispatcher.instance.render(te.getPositiveTile(), x, y, z, partialTicks, destroyStage, false);
            GlStateManager.popMatrix();
        }
        if (te.getNegativeTile() != null) {
            te.getNegativeTile().setWorld(te.getNegativeWorld());
            te.getNegativeTile().setPos(te.getPos());
            TileEntityRendererDispatcher.instance.render(te.getNegativeTile(), x, y, z, partialTicks, destroyStage, false);
        }
    }
}
