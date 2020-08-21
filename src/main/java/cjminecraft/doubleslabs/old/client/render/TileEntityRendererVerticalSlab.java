package cjminecraft.doubleslabs.old.client.render;

import cjminecraft.doubleslabs.old.Registrar;
import cjminecraft.doubleslabs.old.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.old.tileentitiy.TileEntityVerticalSlab;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
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
        ChunkRenderCache world = MinecraftForgeClient.getRegionRenderCache(tile.getWorld(), pos);
        if (world.getBlockState(pos).getBlock() != Registrar.VERTICAL_SLAB)
            return;

        if (tile.getPositiveTile() == null && tile.getNegativeTile() == null)
            return;

        Direction facing = world.getBlockState(pos).get(BlockVerticalSlab.FACING);
        matrixStack.push();

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

        if (tile.getNegativeTile() != null) {
            matrixStack.push();
            matrixStack.translate(0, 0.5d, 0);
            TileEntityRendererDispatcher.instance.renderTileEntity(tile.getNegativeTile(), partialTicks, matrixStack, renderer);
            matrixStack.pop();
        }
        if (tile.getPositiveTile() != null)
            TileEntityRendererDispatcher.instance.renderTileEntity(tile.getPositiveTile(), partialTicks, matrixStack, renderer);

        matrixStack.pop();
    }
}
