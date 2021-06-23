package cjminecraft.doubleslabs.mixin;

import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.client.util.AmbientOcclusionFace;
import cjminecraft.doubleslabs.client.util.DoubleSlabCulling;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public abstract class BlockModelRendererMixin {

    public static boolean renderModelSmooth(BlockModelRenderer renderer, ILightReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack matrixStack, IVertexBuilder buffer, boolean checkSides, Random random, long rand, int combinedOverlay, IModelData modelData) {
        if (model instanceof DynamicSlabBakedModel) {
            boolean flag = false;
            float[] afloat = new float[Direction.values().length * 2];
            BitSet bitset = new BitSet(3);
            AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new AmbientOcclusionFace();

            boolean doubleSlab = false;
            boolean renderNegative = true;
            boolean renderPositive = true;

            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) tile;
                doubleSlab = slab.getPositiveBlockInfo().getBlockState() != null && slab.getNegativeBlockInfo().getBlockState() != null && DynamicSlabBakedModel.useDoubleSlabModel(slab.getPositiveBlockInfo().getBlockState(), slab.getNegativeBlockInfo().getBlockState());
                renderNegative = slab.getNegativeBlockInfo().getBlockState() != null;
                renderPositive = slab.getPositiveBlockInfo().getBlockState() != null;
            }

            for(Direction direction : Direction.values()) {
                random.setSeed(rand);

                if (doubleSlab) {
                    List<BakedQuad> quads = model.getQuads(state, direction, random, modelData);

                    if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldDoubleSlabSideBeRendered(state, world, pos, direction))) {
                        renderQuadsSmooth(renderer, world, state, pos, matrixStack, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlay);
                        flag = true;
                    }
                } else {
                    if (renderPositive) {
                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, true);
                        List<BakedQuad> quads = model.getQuads(state, direction, random, modelData);

                        if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true))) {
                            renderQuadsSmooth(renderer, world, state, pos, matrixStack, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlay);
                            flag = true;
                        }
                    }
                    if (renderNegative) {
                        List<BakedQuad> quads;
                        if (MinecraftForgeClient.getRenderLayer() == null && model instanceof VerticalSlabBakedModel) {
                            // Handle the block breaking animation for a single vertical slab on the negative half
                            // We must flip the direction of the facing in order to get the correct half rendered
                            BlockState newState = state.with(VerticalSlabBlock.FACING, state.get(VerticalSlabBlock.FACING).getOpposite());
                            quads = ((VerticalSlabBakedModel) model).getModel(newState).getQuads(newState, direction, random, modelData);
                        } else {
                            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                            quads = model.getQuads(state, direction, random, modelData);
                        }

                        if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, false))) {
                            renderQuadsSmooth(renderer, world, state, pos, matrixStack, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlay);
                            flag = true;
                        }
                    }
                }
            }

            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, null);

            random.setSeed(rand);
            List<BakedQuad> list1 = model.getQuads(state, null, random, modelData);
            if (!list1.isEmpty()) {
                renderQuadsSmooth(renderer, world, state, pos, matrixStack, buffer, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlay);
                flag = true;
            }

            return flag;
        }
        return false;
    }

    public static boolean renderModelFlat(BlockModelRenderer renderer, ILightReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack matrixStack, IVertexBuilder buffer, boolean checkSides, Random random, long rand, int combinedOverlay, IModelData modelData) {
        if (model instanceof DynamicSlabBakedModel) {
            boolean flag = false;
            BitSet bitset = new BitSet(3);

            boolean doubleSlab = false;
            boolean renderNegative = true;
            boolean renderPositive = true;

            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) tile;
                doubleSlab = slab.getPositiveBlockInfo().getBlockState() != null && slab.getNegativeBlockInfo().getBlockState() != null && DynamicSlabBakedModel.useDoubleSlabModel(slab.getPositiveBlockInfo().getBlockState(), slab.getNegativeBlockInfo().getBlockState());
                renderNegative = slab.getNegativeBlockInfo().getBlockState() != null;
                renderPositive = slab.getPositiveBlockInfo().getBlockState() != null;
            }

            for(Direction direction : Direction.values()) {
                random.setSeed(rand);

                if (doubleSlab) {
                    List<BakedQuad> list = model.getQuads(state, direction, random, modelData);

                    if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldDoubleSlabSideBeRendered(state, world, pos, direction))) {
                        int i = WorldRenderer.getPackedLightmapCoords(world, state, pos.offset(direction));
                        renderQuadsFlat(renderer, world, state, pos, i, combinedOverlay, false, matrixStack, buffer, list, bitset);
                        flag = true;
                    }
                } else {
                    if (renderPositive) {
                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, true);
                        List<BakedQuad> list = model.getQuads(state, direction, random, modelData);

                        if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true))) {
                            int i = WorldRenderer.getPackedLightmapCoords(world, state, pos.offset(direction));
                            renderQuadsFlat(renderer, world, state, pos, i, combinedOverlay, false, matrixStack, buffer, list, bitset);
                            flag = true;
                        }
                    }

                    if (renderNegative) {
                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                        List<BakedQuad> list;
                        if (MinecraftForgeClient.getRenderLayer() == null && model instanceof VerticalSlabBakedModel) {
                            // Handle the block breaking animation for a single vertical slab on the negative half
                            // We must flip the direction of the facing in order to get the correct half rendered
                            BlockState newState = state.with(VerticalSlabBlock.FACING, state.get(VerticalSlabBlock.FACING).getOpposite());
                            list = ((VerticalSlabBakedModel) model).getModel(newState).getQuads(newState, direction, random, modelData);
                        } else {
                            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                            list = model.getQuads(state, direction, random, modelData);
                        }

                        if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, false))) {
                            int i = WorldRenderer.getPackedLightmapCoords(world, state, pos.offset(direction));
                            renderQuadsFlat(renderer, world, state, pos, i, combinedOverlay, false, matrixStack, buffer, list, bitset);
                            flag = true;
                        }
                    }
                }
            }

            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, null);

            random.setSeed(rand);
            List<BakedQuad> list1 = model.getQuads(state, (Direction)null, random, modelData);
            if (!list1.isEmpty()) {
                renderQuadsFlat(renderer, world, state, pos, -1, combinedOverlay, true, matrixStack, buffer, list1, bitset);
                flag = true;
            }

            return flag;
        }
        return false;
    }

    private static void fillQuadBounds(ILightReader blockReaderIn, BlockState stateIn, BlockPos posIn, int[] vertexData, Direction face, @Nullable float[] quadBounds, BitSet boundsFlags) {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        for(int i = 0; i < 4; ++i) {
            float f6 = Float.intBitsToFloat(vertexData[i * 8]);
            float f7 = Float.intBitsToFloat(vertexData[i * 8 + 1]);
            float f8 = Float.intBitsToFloat(vertexData[i * 8 + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (quadBounds != null) {
            quadBounds[Direction.WEST.getIndex()] = f;
            quadBounds[Direction.EAST.getIndex()] = f3;
            quadBounds[Direction.DOWN.getIndex()] = f1;
            quadBounds[Direction.UP.getIndex()] = f4;
            quadBounds[Direction.NORTH.getIndex()] = f2;
            quadBounds[Direction.SOUTH.getIndex()] = f5;
            int j = Direction.values().length;
            quadBounds[Direction.WEST.getIndex() + j] = 1.0F - f;
            quadBounds[Direction.EAST.getIndex() + j] = 1.0F - f3;
            quadBounds[Direction.DOWN.getIndex() + j] = 1.0F - f1;
            quadBounds[Direction.UP.getIndex() + j] = 1.0F - f4;
            quadBounds[Direction.NORTH.getIndex() + j] = 1.0F - f2;
            quadBounds[Direction.SOUTH.getIndex() + j] = 1.0F - f5;
        }

        float f9 = 1.0E-4F;
        float f10 = 0.9999F;
        switch(face) {
            case DOWN:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f1 == f4 && (f1 < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
                break;
            case UP:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f1 == f4 && (f4 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
                break;
            case NORTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, f2 == f5 && (f2 < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
                break;
            case SOUTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, f2 == f5 && (f5 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
                break;
            case WEST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f == f3 && (f < 1.0E-4F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
                break;
            case EAST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f == f3 && (f3 > 0.9999F || stateIn.isCollisionShapeOpaque(blockReaderIn, posIn)));
        }

    }

    private static void renderQuadsSmooth(BlockModelRenderer renderer, ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, List<BakedQuad> list, float[] quadBounds, BitSet bitSet, AmbientOcclusionFace aoFace, int combinedOverlayIn) {
        for(BakedQuad bakedquad : list) {
            fillQuadBounds(blockAccessIn, stateIn, posIn, bakedquad.getVertexData(), bakedquad.getFace(), quadBounds, bitSet);
            aoFace.updateVertexBrightness(blockAccessIn, stateIn, posIn, bakedquad.getFace(), quadBounds, bitSet);
            renderQuadSmooth(renderer, blockAccessIn, stateIn, posIn, buffer, matrixStackIn.getLast(), bakedquad, aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[3], aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3], combinedOverlayIn);
        }
    }

    private static void renderQuadSmooth(BlockModelRenderer renderer, ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, IVertexBuilder buffer, MatrixStack.Entry matrixEntry, BakedQuad quadIn, float colorMul0, float colorMul1, float colorMul2, float colorMul3, int brightness0, int brightness1, int brightness2, int brightness3, int combinedOverlayIn) {
        float f;
        float f1;
        float f2;
        if (quadIn.hasTintIndex()) {
            int i = renderer.blockColors.getColor(stateIn, blockAccessIn, posIn, quadIn.getTintIndex());
            f = (float)(i >> 16 & 255) / 255.0F;
            f1 = (float)(i >> 8 & 255) / 255.0F;
            f2 = (float)(i & 255) / 255.0F;
        } else {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
        }
        // FORGE: Apply diffuse lighting at render-time instead of baking it in
        if (quadIn.shouldApplyDiffuseLighting()) {
            float l = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quadIn.getFace());
            f *= l;
            f1 *= l;
            f2 *= l;
        }

        buffer.addQuad(matrixEntry, quadIn, new float[]{colorMul0, colorMul1, colorMul2, colorMul3}, f, f1, f2, new int[]{brightness0, brightness1, brightness2, brightness3}, combinedOverlayIn, true);
    }

    private static void renderQuadsFlat(BlockModelRenderer renderer, ILightReader blockAccessIn, BlockState stateIn, BlockPos posIn, int brightnessIn, int combinedOverlayIn, boolean ownBrightness, MatrixStack matrixStackIn, IVertexBuilder buffer, List<BakedQuad> list, BitSet bitSet) {
        for(BakedQuad bakedquad : list) {
            if (ownBrightness) {
                fillQuadBounds(blockAccessIn, stateIn, posIn, bakedquad.getVertexData(), bakedquad.getFace(), null, bitSet);
                BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
                brightnessIn = WorldRenderer.getPackedLightmapCoords(blockAccessIn, stateIn, blockpos);
            }

            renderQuadSmooth(renderer, blockAccessIn, stateIn, posIn, buffer, matrixStackIn.getLast(), bakedquad, 1.0F, 1.0F, 1.0F, 1.0F, brightnessIn, brightnessIn, brightnessIn, brightnessIn, combinedOverlayIn);
        }
    }
}
