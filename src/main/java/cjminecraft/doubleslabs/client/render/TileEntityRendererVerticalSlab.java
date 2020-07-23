package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import cjminecraft.doubleslabs.util.Vector3f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Quaternion;

public class TileEntityRendererVerticalSlab extends TileEntitySpecialRenderer<TileEntityVerticalSlab> {

    private static final Quaternion NORTH_ROTATION = Vector3f.XP.rotationDegrees(90).toLWJGLQuaternion();
    private static final Quaternion SOUTH_ROTATION = Vector3f.XN.rotationDegrees(90).toLWJGLQuaternion();
    private static final Quaternion WEST_ROTATION = Vector3f.ZN.rotationDegrees(90).toLWJGLQuaternion();
    private static final Quaternion EAST_ROTATION = Vector3f.ZP.rotationDegrees(90).toLWJGLQuaternion();

    @Override
    public void render(TileEntityVerticalSlab te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockVerticalSlab.FACING);
        switch (facing) {
            case NORTH:
                GlStateManager.rotate(NORTH_ROTATION);
                GlStateManager.translate(0.0, 0.0, -1.0);
                break;
            case SOUTH:
                GlStateManager.rotate(SOUTH_ROTATION);
                GlStateManager.translate(0.0, -1.0, 0.0);
                break;
            case WEST:
                GlStateManager.rotate(WEST_ROTATION);
                GlStateManager.translate(-1.0, 0.0, 0.0);
                break;
            case EAST:
                GlStateManager.rotate(EAST_ROTATION);
                GlStateManager.translate(0.0, -1.0, 0.0);
                break;
        }
        if (te.getNegativeTile() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0.5d, 0);
            te.getNegativeTile().setWorld(te.getNegativeWorld());
            te.getNegativeTile().setPos(te.getPos());
            TileEntityRendererDispatcher.instance.render(te.getNegativeTile(), x, y, z, partialTicks, destroyStage, alpha);
            GlStateManager.popMatrix();
        }
        if (te.getPositiveTile() != null) {
            te.getPositiveTile().setWorld(te.getPositiveWorld());
            te.getPositiveTile().setPos(te.getPos());
            TileEntityRendererDispatcher.instance.render(te.getPositiveTile(), x, y, z, partialTicks, destroyStage, alpha);
        }
        GlStateManager.popMatrix();
    }
}
