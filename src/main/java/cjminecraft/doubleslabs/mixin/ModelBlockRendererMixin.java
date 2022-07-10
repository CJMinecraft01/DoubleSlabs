package cjminecraft.doubleslabs.mixin;

import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.client.util.AmbientOcclusionFace;
import cjminecraft.doubleslabs.client.util.DoubleSlabCulling;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ModelBlockRendererMixin {

    private static final Direction[] DIRECTIONS = Direction.values();

    public static boolean tesselateWithAO(ModelBlockRenderer renderer, BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack stack, VertexConsumer buffer, boolean checkSides, Random random, long rand, int combinedOverlay, IModelData modelData) {
        if (model instanceof DynamicSlabBakedModel) {
            boolean flag = false;
            float[] afloat = new float[DIRECTIONS.length * 2];
            BitSet bitset = new BitSet(3);
            AmbientOcclusionFace modelblockrenderer$ambientocclusionface = new AmbientOcclusionFace();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();

            boolean doubleSlab = false;
            boolean renderNegative = true;
            boolean renderPositive = true;

            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) entity;
                doubleSlab = slab.getPositiveBlockInfo().getBlockState() != null && slab.getNegativeBlockInfo().getBlockState() != null && DynamicSlabBakedModel.useDoubleSlabModel(slab.getPositiveBlockInfo().getBlockState(), slab.getNegativeBlockInfo().getBlockState());
                renderNegative = slab.getNegativeBlockInfo().getBlockState() != null;
                renderPositive = slab.getPositiveBlockInfo().getBlockState() != null;
            }

            for (Direction direction : DIRECTIONS) {
                random.setSeed(rand);

                if (doubleSlab) {
                    List<BakedQuad> list = model.getQuads(state, direction, random, modelData);
                    if (!list.isEmpty()) {
                        blockpos$mutableblockpos.setWithOffset(pos, direction);
                        if (!checkSides || DoubleSlabCulling.shouldDoubleSlabSideBeRendered(state, world, pos, direction)) {
                            renderModelFaceAO(renderer, world, state, pos, stack, buffer, list, afloat, bitset, modelblockrenderer$ambientocclusionface, combinedOverlay);
                            flag = true;
                        }
                    }
                } else {
                    if (renderPositive) {
                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, true);
                        List<BakedQuad> list = model.getQuads(state, direction, random, modelData);

                        if (!list.isEmpty()) {
                            blockpos$mutableblockpos.setWithOffset(pos, direction);
                            if (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true)) {
                                renderModelFaceAO(renderer, world, state, pos, stack, buffer, list, afloat, bitset, modelblockrenderer$ambientocclusionface, combinedOverlay);
                                flag = true;
                            }
                        }
                    }
                    if (renderNegative) {
                        List<BakedQuad> list;
                        if (MinecraftForgeClient.getRenderType() == null && model instanceof VerticalSlabBakedModel) {
                            // Handle the block breaking animation for a single vertical slab on the negative half
                            // We must flip the direction of the facing in order to get the correct half rendered
                            BlockState newState = state.setValue(VerticalSlabBlock.FACING, state.getValue(VerticalSlabBlock.FACING).getOpposite());
                            list = ((VerticalSlabBakedModel) model).getModel(newState).getQuads(newState, direction, random, modelData);
                        } else {
                            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                            list = model.getQuads(state, direction, random, modelData);
                        }

                        if (!list.isEmpty()) {
                            blockpos$mutableblockpos.setWithOffset(pos, direction);
                            if (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, false)) {
                                renderModelFaceAO(renderer, world, state, pos, stack, buffer, list, afloat, bitset, modelblockrenderer$ambientocclusionface, combinedOverlay);
                                flag = true;
                            }
                        }
                    }
                }
            }

            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, null);

            random.setSeed(rand);
            List<BakedQuad> list1 = model.getQuads(state, null, random, modelData);
            if (!list1.isEmpty()) {
                renderModelFaceAO(renderer, world, state, pos, stack, buffer, list1, afloat, bitset, modelblockrenderer$ambientocclusionface, combinedOverlay);
                flag = true;
            }

            return flag;
        }
        return false;
    }

    public static boolean tesselateWithoutAO(ModelBlockRenderer renderer, BlockAndTintGetter world, BakedModel model, BlockState state, BlockPos pos, PoseStack stack, VertexConsumer buffer, boolean checkSides, Random random, long rand, int combinedLight, IModelData modelData) {
        if (model instanceof DynamicSlabBakedModel) {
            boolean flag = false;
            BitSet bitset = new BitSet(3);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable();

            boolean doubleSlab = false;
            boolean renderNegative = true;
            boolean renderPositive = true;

            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) entity;
                doubleSlab = slab.getPositiveBlockInfo().getBlockState() != null && slab.getNegativeBlockInfo().getBlockState() != null && DynamicSlabBakedModel.useDoubleSlabModel(slab.getPositiveBlockInfo().getBlockState(), slab.getNegativeBlockInfo().getBlockState());
                renderNegative = slab.getNegativeBlockInfo().getBlockState() != null;
                renderPositive = slab.getPositiveBlockInfo().getBlockState() != null;
            }

            for (Direction direction : DIRECTIONS) {
                random.setSeed(rand);

                if (doubleSlab) {
                    List<BakedQuad> list = model.getQuads(state, direction, random, modelData);
                    if (!list.isEmpty()) {
                        blockpos$mutableblockpos.setWithOffset(pos, direction);
                        if (!checkSides || DoubleSlabCulling.shouldDoubleSlabSideBeRendered(state, world, pos, direction)) {
                            int i = LevelRenderer.getLightColor(world, state, blockpos$mutableblockpos);
                            renderModelFaceFlat(renderer, world, state, pos, i, combinedLight, false, stack, buffer, list, bitset);
                            flag = true;
                        }
                    }
                } else {
                    if (renderPositive) {
                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, true);
                        List<BakedQuad> list = model.getQuads(state, direction, random, modelData);
                        if (!list.isEmpty()) {
                            blockpos$mutableblockpos.setWithOffset(pos, direction);
                            if (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true)) {
                                int i = LevelRenderer.getLightColor(world, state, blockpos$mutableblockpos);
                                renderModelFaceFlat(renderer, world, state, pos, i, combinedLight, false, stack, buffer, list, bitset);
                                flag = true;
                            }
                        }
                    }
                    if (renderNegative) {
                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                        List<BakedQuad> list;
                        if (MinecraftForgeClient.getRenderType() == null && model instanceof VerticalSlabBakedModel) {
                            // Handle the block breaking animation for a single vertical slab on the negative half
                            // We must flip the direction of the facing in order to get the correct half rendered
                            BlockState newState = state.setValue(VerticalSlabBlock.FACING, state.getValue(VerticalSlabBlock.FACING).getOpposite());
                            list = ((VerticalSlabBakedModel) model).getModel(newState).getQuads(newState, direction, random, modelData);
                        } else {
                            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                            list = model.getQuads(state, direction, random, modelData);
                        }

                        if (!list.isEmpty()) {
                            blockpos$mutableblockpos.setWithOffset(pos, direction);
                            if (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, false)) {
                                int i = LevelRenderer.getLightColor(world, state, blockpos$mutableblockpos);
                                renderModelFaceFlat(renderer, world, state, pos, i, combinedLight, false, stack, buffer, list, bitset);
                                flag = true;
                            }
                        }
                    }
                }
            }

            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, null);

            random.setSeed(rand);
            List<BakedQuad> list1 = model.getQuads(state, null, random, modelData);
            if (!list1.isEmpty()) {
                renderModelFaceFlat(renderer, world, state, pos, -1, combinedLight, true, stack, buffer, list1, bitset);
                flag = true;
            }

            return flag;
        }
        return false;
    }

    private static void renderModelFaceAO(ModelBlockRenderer renderer, BlockAndTintGetter world, BlockState state, BlockPos pos, PoseStack stack, VertexConsumer buffer, List<BakedQuad> quads, float[] quadBounds, BitSet bitSet, AmbientOcclusionFace aoFace, int combinedOverlay) {
        for(BakedQuad bakedquad : quads) {
            calculateShape(world, state, pos, bakedquad.getVertices(), bakedquad.getDirection(), quadBounds, bitSet);
            aoFace.calculate(world, state, pos, bakedquad.getDirection(), quadBounds, bitSet, bakedquad.isShade());
            putQuadData(renderer, world, state, pos, buffer, stack.last(), bakedquad, aoFace.brightness[0], aoFace.brightness[1], aoFace.brightness[2], aoFace.brightness[3], aoFace.lightmap[0], aoFace.lightmap[1], aoFace.lightmap[2], aoFace.lightmap[3], combinedOverlay);
        }

    }

    private static void renderModelFaceFlat(ModelBlockRenderer renderer, BlockAndTintGetter world, BlockState state, BlockPos pos, int lightmap1, int lightmap2, boolean colored, PoseStack stack, VertexConsumer buffer, List<BakedQuad> quads, BitSet bitSet) {
        for(BakedQuad bakedquad : quads) {
            if (colored) {
                calculateShape(world, state, pos, bakedquad.getVertices(), bakedquad.getDirection(), (float[])null, bitSet);
                BlockPos blockpos = bitSet.get(0) ? pos.relative(bakedquad.getDirection()) : pos;
                lightmap1 = LevelRenderer.getLightColor(world, state, blockpos);
            }

            float f = world.getShade(bakedquad.getDirection(), bakedquad.isShade());
            putQuadData(renderer, world, state, pos, buffer, stack.last(), bakedquad, f, f, f, f, lightmap1, lightmap1, lightmap1, lightmap1, lightmap2);
        }

    }

    private static void putQuadData(ModelBlockRenderer renderer, BlockAndTintGetter p_111024_, BlockState p_111025_, BlockPos p_111026_, VertexConsumer p_111027_, PoseStack.Pose p_111028_, BakedQuad p_111029_, float p_111030_, float p_111031_, float p_111032_, float p_111033_, int p_111034_, int p_111035_, int p_111036_, int p_111037_, int p_111038_) {
        float f;
        float f1;
        float f2;
        if (p_111029_.isTinted()) {
            int i = renderer.blockColors.getColor(p_111025_, p_111024_, p_111026_, p_111029_.getTintIndex());
            f = (float)(i >> 16 & 255) / 255.0F;
            f1 = (float)(i >> 8 & 255) / 255.0F;
            f2 = (float)(i & 255) / 255.0F;
        } else {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
        }

        p_111027_.putBulkData(p_111028_, p_111029_, new float[]{p_111030_, p_111031_, p_111032_, p_111033_}, f, f1, f2, new int[]{p_111034_, p_111035_, p_111036_, p_111037_}, p_111038_, true);
    }

    private static void calculateShape(BlockAndTintGetter p_111040_, BlockState p_111041_, BlockPos p_111042_, int[] p_111043_, Direction p_111044_, @Nullable float[] p_111045_, BitSet p_111046_) {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        for(int i = 0; i < 4; ++i) {
            float f6 = Float.intBitsToFloat(p_111043_[i * 8]);
            float f7 = Float.intBitsToFloat(p_111043_[i * 8 + 1]);
            float f8 = Float.intBitsToFloat(p_111043_[i * 8 + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (p_111045_ != null) {
            p_111045_[Direction.WEST.get3DDataValue()] = f;
            p_111045_[Direction.EAST.get3DDataValue()] = f3;
            p_111045_[Direction.DOWN.get3DDataValue()] = f1;
            p_111045_[Direction.UP.get3DDataValue()] = f4;
            p_111045_[Direction.NORTH.get3DDataValue()] = f2;
            p_111045_[Direction.SOUTH.get3DDataValue()] = f5;
            int j = DIRECTIONS.length;
            p_111045_[Direction.WEST.get3DDataValue() + j] = 1.0F - f;
            p_111045_[Direction.EAST.get3DDataValue() + j] = 1.0F - f3;
            p_111045_[Direction.DOWN.get3DDataValue() + j] = 1.0F - f1;
            p_111045_[Direction.UP.get3DDataValue() + j] = 1.0F - f4;
            p_111045_[Direction.NORTH.get3DDataValue() + j] = 1.0F - f2;
            p_111045_[Direction.SOUTH.get3DDataValue() + j] = 1.0F - f5;
        }

        float f9 = 1.0E-4F;
        float f10 = 0.9999F;
        switch(p_111044_) {
            case DOWN:
                p_111046_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                p_111046_.set(0, f1 == f4 && (f1 < 1.0E-4F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case UP:
                p_111046_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                p_111046_.set(0, f1 == f4 && (f4 > 0.9999F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case NORTH:
                p_111046_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                p_111046_.set(0, f2 == f5 && (f2 < 1.0E-4F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case SOUTH:
                p_111046_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                p_111046_.set(0, f2 == f5 && (f5 > 0.9999F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case WEST:
                p_111046_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                p_111046_.set(0, f == f3 && (f < 1.0E-4F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case EAST:
                p_111046_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                p_111046_.set(0, f == f3 && (f3 > 0.9999F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
        }

    }

}