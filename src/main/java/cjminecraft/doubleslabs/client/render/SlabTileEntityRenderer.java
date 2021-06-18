package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.MinecraftForgeClient;

public class SlabTileEntityRenderer extends TileEntityRenderer<SlabTileEntity> {

    private static final Quaternion NORTH_ROTATION = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion SOUTH_ROTATION = Vector3f.XN.rotationDegrees(90);
    private static final Quaternion WEST_ROTATION = Vector3f.ZN.rotationDegrees(90);
    private static final Quaternion EAST_ROTATION = Vector3f.ZP.rotationDegrees(90);

    public SlabTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(SlabTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        BlockPos pos = tile.getPos();
        ChunkRenderCache world = MinecraftForgeClient.getRegionRenderCache(tile.getWorld(), pos);
        if (world == null)
            return;
        BlockState state = world.getBlockState(pos);
        if (tile.getPositiveBlockInfo().getTileEntity() == null && tile.getNegativeBlockInfo().getTileEntity() == null)
            return;
        if (tile.getPositiveBlockInfo().getTileEntity() != null)
            tile.getPositiveBlockInfo().getTileEntity().setWorldAndPos(tile.getPositiveBlockInfo().getWorld(), tile.getPositiveBlockInfo().getPos());
        if (tile.getNegativeBlockInfo().getTileEntity() != null)
            tile.getNegativeBlockInfo().getTileEntity().setWorldAndPos(tile.getNegativeBlockInfo().getWorld(), tile.getNegativeBlockInfo().getPos());
        if (state.getBlock() == DSBlocks.DOUBLE_SLAB.get()) {
            if (tile.getPositiveBlockInfo().getTileEntity() != null) {
                TileEntityRendererDispatcher.instance.renderTileEntity(tile.getPositiveBlockInfo().getTileEntity(), partialTicks, matrixStack, buffer);
            }
            if (tile.getNegativeBlockInfo().getTileEntity() != null)
                TileEntityRendererDispatcher.instance.renderTileEntity(tile.getNegativeBlockInfo().getTileEntity(), partialTicks, matrixStack, buffer);
        } else if (state.getBlock() == DSBlocks.VERTICAL_SLAB.get()) {
            Direction facing = world.getBlockState(pos).get(VerticalSlabBlock.FACING);
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

            if (tile.getNegativeBlockInfo().getTileEntity() != null) {
                TileEntityRendererDispatcher.instance.renderTileEntity(tile.getNegativeBlockInfo().getTileEntity(), partialTicks, matrixStack, buffer);
            }
            if (tile.getPositiveBlockInfo().getTileEntity() != null)
                TileEntityRendererDispatcher.instance.renderTileEntity(tile.getPositiveBlockInfo().getTileEntity(), partialTicks, matrixStack, buffer);

            matrixStack.pop();
        }
    }
}
