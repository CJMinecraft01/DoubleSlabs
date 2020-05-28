package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nonnull;
import java.util.Random;

public class TileEntityRendererVerticalSlab extends TileEntityRenderer<TileEntityVerticalSlab> {

    private static final Quaternion NORTH_ROTATION = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion SOUTH_ROTATION = Vector3f.XN.rotationDegrees(90);
    private static final Quaternion WEST_ROTATION = Vector3f.ZN.rotationDegrees(90);
    private static final Quaternion EAST_ROTATION = Vector3f.ZP.rotationDegrees(90);

    private static BlockRendererDispatcher blockRenderer;

    public TileEntityRendererVerticalSlab(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@Nonnull TileEntityVerticalSlab tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderer, int light, int otherLight) {
        if (blockRenderer == null)
            blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

        BlockPos pos = tile.getPos();
        ILightReader world = MinecraftForgeClient.getRegionRenderCache(tile.getWorld(), pos);
        if (world.getBlockState(pos).getBlock() != Registrar.VERTICAL_SLAB)
            return;

        BlockState negativeState = tile.getNegativeState();
        BlockState positiveState = tile.getPositiveState();

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

        if (negativeState != null)
            blockRenderer.renderModel(negativeState, pos, world, matrixStack, renderer.getBuffer(Atlases.getSolidBlockType()), false, new Random(), EmptyModelData.INSTANCE);
        if (positiveState != null)
            blockRenderer.renderModel(positiveState, pos, world, matrixStack, renderer.getBuffer(Atlases.getSolidBlockType()), false, new Random(), EmptyModelData.INSTANCE);
    }
}
