package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SlabTileEntityRenderer extends TileEntitySpecialRenderer<SlabTileEntity> {

//    private static final Quaternion NORTH_ROTATION = new Quaternion(new Vector3f(1, 0, 0), 90, true);
//    private static final Quaternion SOUTH_ROTATION = new Quaternion(new Vector3f(-1, 0, 0), 90, true);
//    private static final Quaternion WEST_ROTATION = new Quaternion(new Vector3f(0, 0, -1), 90, true);
//    private static final Quaternion EAST_ROTATION = new Quaternion(new Vector3f(0, 0, 1), 90, true);

    @Override
    public void render(SlabTileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        BlockPos pos = tile.getPos();
        World world = tile.getWorld();
        if (world == null)
            return;
        IBlockState state = world.getBlockState(pos);
        if (tile.getPositiveBlockInfo().getTileEntity() == null && tile.getNegativeBlockInfo().getTileEntity() == null)
            return;
        if (tile.getPositiveBlockInfo().getTileEntity() != null)
            tile.getPositiveBlockInfo().getTileEntity().setWorld(tile.getPositiveBlockInfo().getWorld());
        if (tile.getNegativeBlockInfo().getTileEntity() != null)
            tile.getNegativeBlockInfo().getTileEntity().setWorld(tile.getNegativeBlockInfo().getWorld());
        if (state.getBlock() == DSBlocks.DOUBLE_SLAB) {
            if (tile.getPositiveBlockInfo().getTileEntity() != null) {
//                matrixStack.push();
//                GlStateManager.translatef(0, 0.5d, 0);
                TileEntityRendererDispatcher.instance.render(tile.getPositiveBlockInfo().getTileEntity(), partialTicks, destroyStage);
//                matrixStack.pop();
            }
            if (tile.getNegativeBlockInfo().getTileEntity() != null)
                TileEntityRendererDispatcher.instance.render(tile.getNegativeBlockInfo().getTileEntity(), partialTicks, destroyStage);
        } else if (state.getBlock() == DSBlocks.VERTICAL_SLAB) {
            EnumFacing facing = world.getBlockState(pos).getValue(VerticalSlabBlock.FACING);
            GlStateManager.pushMatrix();

            switch (facing) {
                case NORTH:
//                    GlStateManager.translatef(pos.getX(), pos.getY(), pos.getZ());
                    GlStateManager.rotate(90, 1, 0, 0);
                    GlStateManager.translate(0.0f, 0.0f, -1.0f);
                    break;
                case SOUTH:
//                    GlStateManager.translatef(pos.getX(), pos.getY(), pos.getZ());
                    GlStateManager.rotate(90, -1, 0, 0);
                    GlStateManager.translate(0.0f, -1.0f, 0.0f);
                    break;
                case WEST:
//                    GlStateManager.translatef(pos.getX(), pos.getY(), pos.getZ());
                    GlStateManager.rotate(90, 0, 0, -1);
                    GlStateManager.translate(-1.0f, 0.0f, 0.0f);
                    break;
                case EAST:
//                    GlStateManager.translatef(pos.getX(), pos.getY(), pos.getZ());
                    GlStateManager.rotate(90, 0, 0, 1);
                    GlStateManager.translate(0.0f, -1.0f, 0.0f);
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
