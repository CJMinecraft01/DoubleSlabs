package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlabTileEntityRenderer extends TileEntityRenderer<SlabTileEntity> {

    private static final Quaternion NORTH_ROTATION = new Quaternion(new Vector3f(1, 0, 0), 90, true);
    private static final Quaternion SOUTH_ROTATION = new Quaternion(new Vector3f(-1, 0, 0), 90, true);
    private static final Quaternion WEST_ROTATION = new Quaternion(new Vector3f(0, 0, -1), 90, true);
    private static final Quaternion EAST_ROTATION = new Quaternion(new Vector3f(0, 0, 1), 90, true);

    @Override
    public void render(SlabTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(tile, x, y, z, partialTicks, destroyStage);
        BlockPos pos = tile.getPos();
        World world = tile.getWorld();
        if (world == null)
            return;
        BlockState state = world.getBlockState(pos);
        if (tile.getPositiveBlockInfo().getTileEntity() == null && tile.getNegativeBlockInfo().getTileEntity() == null)
            return;
        if (tile.getPositiveBlockInfo().getTileEntity() != null)
            tile.getPositiveBlockInfo().getTileEntity().setWorld(tile.getPositiveBlockInfo().getWorld());
        if (tile.getNegativeBlockInfo().getTileEntity() != null)
            tile.getNegativeBlockInfo().getTileEntity().setWorld(tile.getNegativeBlockInfo().getWorld());
        if (state.getBlock() == DSBlocks.DOUBLE_SLAB.get()) {
            if (tile.getPositiveBlockInfo().getTileEntity() != null) {
//                matrixStack.push();
//                GlStateManager.translatef(0, 0.5d, 0);
                TileEntityRendererDispatcher.instance.render(tile.getPositiveBlockInfo().getTileEntity(), partialTicks, destroyStage);
//                matrixStack.pop();
            }
            if (tile.getNegativeBlockInfo().getTileEntity() != null)
                TileEntityRendererDispatcher.instance.render(tile.getNegativeBlockInfo().getTileEntity(), partialTicks, destroyStage);
        } else if (state.getBlock() == DSBlocks.VERTICAL_SLAB.get()) {
            Direction facing = world.getBlockState(pos).get(VerticalSlabBlock.FACING);
            GlStateManager.pushMatrix();

            switch (facing) {
                case NORTH:
                    GlStateManager.rotatef(NORTH_ROTATION.getX(), NORTH_ROTATION.getY(), NORTH_ROTATION.getZ(), NORTH_ROTATION.getW());
                    GlStateManager.translatef(0.0f, 0.0f, -1.0f);
                    break;
                case SOUTH:
                    GlStateManager.rotatef(SOUTH_ROTATION.getX(), SOUTH_ROTATION.getY(), SOUTH_ROTATION.getZ(), SOUTH_ROTATION.getW());
                    GlStateManager.translatef(0.0f, -1.0f, 0.0f);
                    break;
                case WEST:
                    GlStateManager.rotatef(WEST_ROTATION.getX(), WEST_ROTATION.getY(), WEST_ROTATION.getZ(), WEST_ROTATION.getW());
                    GlStateManager.translatef(-1.0f, 0.0f, 0.0f);
                    break;
                case EAST:
                    GlStateManager.rotatef(EAST_ROTATION.getX(), EAST_ROTATION.getY(), EAST_ROTATION.getZ(), EAST_ROTATION.getW());
                    GlStateManager.translatef(0.0f, -1.0f, 0.0f);
                    break;
            }

            if (tile.getNegativeBlockInfo().getTileEntity() != null) {
//                matrixStack.push();
//                GlStateManager.translatef(0, 0.5d, 0);
                TileEntityRendererDispatcher.instance.render(tile.getNegativeBlockInfo().getTileEntity(), partialTicks, destroyStage);
//                matrixStack.pop();
            }
            if (tile.getPositiveBlockInfo().getTileEntity() != null)
                TileEntityRendererDispatcher.instance.render(tile.getPositiveBlockInfo().getTileEntity(), partialTicks, destroyStage);

            GlStateManager.popMatrix();
        }
    }
}
