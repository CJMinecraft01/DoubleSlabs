package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.CampfireTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class RaisedCampfireTileEntityRenderer extends TileEntityRenderer<RaisedCampfireTileEntity> {

    private final CampfireTileEntityRenderer renderer;

    public RaisedCampfireTileEntityRenderer() {
        this.renderer = new CampfireTileEntityRenderer();
    }

    @Override
    public void render(RaisedCampfireTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0, 0.5f, 0);
        this.renderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
        GlStateManager.popMatrix();
    }
}
