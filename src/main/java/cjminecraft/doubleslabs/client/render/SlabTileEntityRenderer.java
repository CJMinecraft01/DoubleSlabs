package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;

public class SlabTileEntityRenderer implements BlockEntityRenderer<SlabTileEntity> {

    private static final Quaternion NORTH_ROTATION = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion SOUTH_ROTATION = Vector3f.XN.rotationDegrees(90);
    private static final Quaternion WEST_ROTATION = Vector3f.ZN.rotationDegrees(90);
    private static final Quaternion EAST_ROTATION = Vector3f.ZP.rotationDegrees(90);

    public SlabTileEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SlabTileEntity slab, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        BlockPos pos = slab.getBlockPos();
        RenderChunkRegion world = MinecraftForgeClient.getRegionRenderCache(slab.getLevel(), pos);
        if (world == null)
            return;
        BlockState state = world.getBlockState(pos);
        if (slab.getPositiveBlockInfo().getBlockEntity() == null && slab.getNegativeBlockInfo().getBlockEntity() == null)
            return;
        if (slab.getPositiveBlockInfo().getBlockEntity() != null)
            slab.getPositiveBlockInfo().getBlockEntity().setLevel(slab.getPositiveBlockInfo().getWorld());
        if (slab.getNegativeBlockInfo().getBlockEntity() != null)
            slab.getNegativeBlockInfo().getBlockEntity().setLevel(slab.getNegativeBlockInfo().getWorld());
        if (state.getBlock() == DSBlocks.DOUBLE_SLAB.get()) {
            if (slab.getPositiveBlockInfo().getBlockEntity() != null) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().render(slab.getPositiveBlockInfo().getBlockEntity(), partialTicks, poseStack, buffer);
            }
            if (slab.getNegativeBlockInfo().getBlockEntity() != null)
                Minecraft.getInstance().getBlockEntityRenderDispatcher().render(slab.getNegativeBlockInfo().getBlockEntity(), partialTicks, poseStack, buffer);
        } else if (state.getBlock() == DSBlocks.VERTICAL_SLAB.get()) {
            Direction facing = world.getBlockState(pos).getValue(VerticalSlabBlock.FACING);
            poseStack.pushPose();

            switch (facing) {
                case NORTH:
                    poseStack.mulPose(NORTH_ROTATION);
                    poseStack.translate(0.0, 0.0, -1.0);
                    break;
                case SOUTH:
                    poseStack.mulPose(SOUTH_ROTATION);
                    poseStack.translate(0.0, -1.0, 0.0);
                    break;
                case WEST:
                    poseStack.mulPose(WEST_ROTATION);
                    poseStack.translate(-1.0, 0.0, 0.0);
                    break;
                case EAST:
                    poseStack.mulPose(EAST_ROTATION);
                    poseStack.translate(0.0, -1.0, 0.0);
                    break;
            }

            if (slab.getNegativeBlockInfo().getBlockEntity() != null) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().render(slab.getNegativeBlockInfo().getBlockEntity(), partialTicks, poseStack, buffer);
            }
            if (slab.getPositiveBlockInfo().getBlockEntity() != null)
                Minecraft.getInstance().getBlockEntityRenderDispatcher().render(slab.getPositiveBlockInfo().getBlockEntity(), partialTicks, poseStack, buffer);

            poseStack.popPose();
        }
    }
}
