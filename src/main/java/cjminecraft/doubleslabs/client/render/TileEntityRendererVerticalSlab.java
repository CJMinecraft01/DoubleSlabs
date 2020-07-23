package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class TileEntityRendererVerticalSlab extends TileEntityRenderer<TileEntityVerticalSlab> {

    private static final FloatBuffer BUF_FLOAT_16 = BufferUtils.createFloatBuffer(16);

    private static final FloatBuffer NORTH_ROTATION = quaternionToGlMatrix(BUF_FLOAT_16, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90, true));
    private static final FloatBuffer SOUTH_ROTATION = quaternionToGlMatrix(BUF_FLOAT_16, new Quaternion(new Vector3f(-1.0F, 0.0F, 0.0F), 90, true));
    private static final FloatBuffer WEST_ROTATION = quaternionToGlMatrix(BUF_FLOAT_16, new Quaternion(new Vector3f(0.0F, 0.0F, -1.0F), 90, true));
    private static final FloatBuffer EAST_ROTATION = quaternionToGlMatrix(BUF_FLOAT_16, new Quaternion(new Vector3f(0.0F, 0.0F, 1.0F), 90, true));
    
    private static FloatBuffer quaternionToGlMatrix(FloatBuffer buffer, Quaternion quaternion) {
        buffer.clear();
        float f = quaternion.getX() * quaternion.getX();
        float f1 = quaternion.getX() * quaternion.getY();
        float f2 = quaternion.getX() * quaternion.getZ();
        float f3 = quaternion.getX() * quaternion.getW();
        float f4 = quaternion.getY() * quaternion.getY();
        float f5 = quaternion.getY() * quaternion.getZ();
        float f6 = quaternion.getY() * quaternion.getW();
        float f7 = quaternion.getZ() * quaternion.getZ();
        float f8 = quaternion.getZ() * quaternion.getW();
        buffer.put(1.0F - 2.0F * (f4 + f7));
        buffer.put(2.0F * (f1 + f8));
        buffer.put(2.0F * (f2 - f6));
        buffer.put(0.0F);
        buffer.put(2.0F * (f1 - f8));
        buffer.put(1.0F - 2.0F * (f + f7));
        buffer.put(2.0F * (f5 + f3));
        buffer.put(0.0F);
        buffer.put(2.0F * (f2 + f6));
        buffer.put(2.0F * (f5 - f3));
        buffer.put(1.0F - 2.0F * (f + f4));
        buffer.put(0.0F);
        buffer.put(0.0F);
        buffer.put(0.0F);
        buffer.put(0.0F);
        buffer.put(1.0F);
        buffer.rewind();
        return buffer;
    }

    @Override
    public void render(TileEntityVerticalSlab te, double x, double y, double z, float partialTicks, int destroyStage) {
        // TODO add vertical slab TER
        return;
        /*
        GlStateManager.pushMatrix();
        Direction facing = te.getWorld().getBlockState(te.getPos()).get(BlockVerticalSlab.FACING);
        GlStateManager.translatef(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
        switch (facing) {
            case NORTH:
//                GlStateManager.rotatef(90, 1, 0, 0);
                GlStateManager.multMatrix(NORTH_ROTATION);
//                GlStateManager.translatef(0.0f, 0.0f, -1.0f);
                break;
            case SOUTH:
//                GlStateManager.rotatef(90, -1, 0, 0);
                GlStateManager.multMatrix(SOUTH_ROTATION);
//                GlStateManager.translatef(0.0f, -1.0f, 0.0f);
                break;
            case WEST:
//                GlStateManager.rotatef(90, 0, 0, -1);
                GlStateManager.multMatrix(WEST_ROTATION);
//                GlStateManager.translatef(-1.0f, 0.0f, 0.0f);
                break;
            case EAST:
//                GlStateManager.rotatef(90, 0, 0, 1);
                GlStateManager.multMatrix(EAST_ROTATION);
//                GlStateManager.translatef(0.0f, -1.0f, 0.0f);
                break;
        }
        GlStateManager.translatef(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
//        GlStateManager.translatef((float)x, (float)y, (float)z);
//        GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        if (te.getNegativeTile() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0, 0.5f, 0);
            te.getNegativeTile().setWorld(te.getNegativeWorld());
            te.getNegativeTile().setPos(te.getPos());
            TileEntityRendererDispatcher.instance.render(te.getNegativeTile(), x, y, z, partialTicks, destroyStage, false);
            GlStateManager.popMatrix();
        }
        if (te.getPositiveTile() != null) {
            te.getPositiveTile().setWorld(te.getPositiveWorld());
            te.getPositiveTile().setPos(te.getPos());
            TileEntityRendererDispatcher.instance.render(te.getPositiveTile(), x, y, z, partialTicks, destroyStage, false);
        }
//        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

         */
    }
}
