package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;

import javax.annotation.Nonnull;

public class TileEntityRendererVerticalSlab extends TileEntityRenderer<TileEntityVerticalSlab> {

    private static final Quaternion NORTH_ROTATION = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion SOUTH_ROTATION = Vector3f.XN.rotationDegrees(90);
    private static final Quaternion WEST_ROTATION = Vector3f.ZN.rotationDegrees(90);
    private static final Quaternion EAST_ROTATION = Vector3f.ZP.rotationDegrees(90);

    public TileEntityRendererVerticalSlab(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@Nonnull TileEntityVerticalSlab tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderer, int light, int otherLight) {
        BlockPos pos = tile.getPos();
        ILightReader world = MinecraftForgeClient.getRegionRenderCache(tile.getWorld(), pos);
        if (world.getBlockState(pos).getBlock() != Registrar.VERTICAL_SLAB)
            return;

        if (tile.getPositiveTile() == null && tile.getNegativeTile() == null)
            return;

        Direction facing = world.getBlockState(pos).get(BlockVerticalSlab.FACING);

        switch (facing) {
            case NORTH:
                matrixStack.rotate(NORTH_ROTATION);
                matrixStack.translate(0.0, 0.0, -1.0);
                break;
            case SOUTH:
                matrixStack.rotate(SOUTH_ROTATION);
                matrixStack.translate(0.0, -1.0, 0.0);
                break;
            case WEST:
                matrixStack.rotate(WEST_ROTATION);
                matrixStack.translate(-1.0, 0.0, 0.0);
                break;
            case EAST:
                matrixStack.rotate(EAST_ROTATION);
                matrixStack.translate(0.0, -1.0, 0.0);
                break;
        }

        if (tile.getPositiveTile() != null)
            TileEntityRendererDispatcher.instance.renderTileEntity(tile.getPositiveTile(), partialTicks, matrixStack, renderer);
        if (tile.getNegativeTile() != null)
            TileEntityRendererDispatcher.instance.renderTileEntity(tile.getNegativeTile(), partialTicks, matrixStack, renderer);
    }
}
