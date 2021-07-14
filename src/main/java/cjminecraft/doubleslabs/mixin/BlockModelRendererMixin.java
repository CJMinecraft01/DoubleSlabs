package cjminecraft.doubleslabs.mixin;

import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.client.util.AmbientOcclusionFace;
import cjminecraft.doubleslabs.client.util.DoubleSlabCulling;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IEnviromentBlockReader;
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

    public static boolean renderModelSmooth(BlockModelRenderer renderer, IEnviromentBlockReader world, IBakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, Random random, long rand, IModelData modelData) {
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
                        renderQuadsSmooth(renderer, world, state, pos, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                        flag = true;
                    }
                } else {
                    if (renderPositive) {
                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, true);
                        List<BakedQuad> quads = model.getQuads(state, direction, random, modelData);

                        if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true))) {
                            renderQuadsSmooth(renderer, world, state, pos, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface);
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
                            renderQuadsSmooth(renderer, world, state, pos, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                            flag = true;
                        }
                    }
                }
            }

            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, null);

            random.setSeed(rand);
            List<BakedQuad> list1 = model.getQuads(state, null, random, modelData);
            if (!list1.isEmpty()) {
                renderQuadsSmooth(renderer, world, state, pos, buffer, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                flag = true;
            }

            return flag;
        }
        return false;
    }

    public static boolean renderModelFlat(BlockModelRenderer renderer, IEnviromentBlockReader world, IBakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, Random random, long rand, IModelData modelData) {
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
                        int i = state.getPackedLightmapCoords(world, pos.offset(direction));
                        renderQuadsFlat(renderer, world, state, pos, i, false, buffer, list, bitset);
                        flag = true;
                    }
                } else {
                    if (renderPositive) {
                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, true);
                        List<BakedQuad> list = model.getQuads(state, direction, random, modelData);

                        if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true))) {
                            int i = state.getPackedLightmapCoords(world, pos.offset(direction));
                            renderQuadsFlat(renderer, world, state, pos, i, false, buffer, list, bitset);
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
                            int i = state.getPackedLightmapCoords(world, pos.offset(direction));
                            renderQuadsFlat(renderer, world, state, pos, i, false, buffer, list, bitset);
                            flag = true;
                        }
                    }
                }
            }

            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, null);

            random.setSeed(rand);
            List<BakedQuad> list1 = model.getQuads(state, (Direction)null, random, modelData);
            if (!list1.isEmpty()) {
                renderQuadsFlat(renderer, world, state, pos, -1, true, buffer, list1, bitset);
                flag = true;
            }

            return flag;
        }
        return false;
    }

    private static void fillQuadBounds(IEnviromentBlockReader blockReaderIn, BlockState stateIn, BlockPos posIn, int[] vertexData, Direction face, @Nullable float[] quadBounds, BitSet boundsFlags) {
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
                boundsFlags.set(0, f1 == f4 && (f1 < 1.0E-4F || stateIn.func_224756_o(blockReaderIn, posIn)));
                break;
            case UP:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f1 == f4 && (f4 > 0.9999F || stateIn.func_224756_o(blockReaderIn, posIn)));
                break;
            case NORTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, f2 == f5 && (f2 < 1.0E-4F || stateIn.func_224756_o(blockReaderIn, posIn)));
                break;
            case SOUTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, f2 == f5 && (f5 > 0.9999F || stateIn.func_224756_o(blockReaderIn, posIn)));
                break;
            case WEST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f == f3 && (f < 1.0E-4F || stateIn.func_224756_o(blockReaderIn, posIn)));
                break;
            case EAST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, f == f3 && (f3 > 0.9999F || stateIn.func_224756_o(blockReaderIn, posIn)));
        }

    }

    private static void renderQuadsSmooth(BlockModelRenderer renderer, IEnviromentBlockReader worldIn, BlockState state, BlockPos pos, BufferBuilder buffer, List<BakedQuad> quads, float[] weights, BitSet bitSet, AmbientOcclusionFace aoFace) {
        Vec3d vec3d = state.getOffset(worldIn, pos);
        double d0 = (double)pos.getX() + vec3d.x;
        double d1 = (double)pos.getY() + vec3d.y;
        double d2 = (double)pos.getZ() + vec3d.z;
        int i = 0;

        for(int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = quads.get(i);
            fillQuadBounds(worldIn, state, pos, bakedquad.getVertexData(), bakedquad.getFace(), weights, bitSet);
            aoFace.updateVertexBrightness(worldIn, state, pos, bakedquad.getFace(), weights, bitSet);
            buffer.addVertexData(bakedquad.getVertexData());
            buffer.putBrightness4(aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3]);
            if(bakedquad.shouldApplyDiffuseLighting()) {
                float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
                aoFace.vertexColorMultiplier[0] *= diffuse;
                aoFace.vertexColorMultiplier[1] *= diffuse;
                aoFace.vertexColorMultiplier[2] *= diffuse;
                aoFace.vertexColorMultiplier[3] *= diffuse;
            }
            if (bakedquad.hasTintIndex()) {
                int k = renderer.blockColors.getColor(state, worldIn, pos, bakedquad.getTintIndex());
                float f = (float)(k >> 16 & 255) / 255.0F;
                float f1 = (float)(k >> 8 & 255) / 255.0F;
                float f2 = (float)(k & 255) / 255.0F;
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[0] * f, aoFace.vertexColorMultiplier[0] * f1, aoFace.vertexColorMultiplier[0] * f2, 4);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[1] * f, aoFace.vertexColorMultiplier[1] * f1, aoFace.vertexColorMultiplier[1] * f2, 3);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[2] * f, aoFace.vertexColorMultiplier[2] * f1, aoFace.vertexColorMultiplier[2] * f2, 2);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[3] * f, aoFace.vertexColorMultiplier[3] * f1, aoFace.vertexColorMultiplier[3] * f2, 1);
            } else {
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], 4);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], 3);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], 2);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], 1);
            }

            buffer.putPosition(d0, d1, d2);
        }
    }

    private static void renderQuadsFlat(BlockModelRenderer renderer, IEnviromentBlockReader world, BlockState state, BlockPos pos, int brightness, boolean ownBrightness, BufferBuilder buffer, List<BakedQuad> list, BitSet bitSet) {
        Vec3d vec3d = state.getOffset(world, pos);
        double d0 = (double)pos.getX() + vec3d.x;
        double d1 = (double)pos.getY() + vec3d.y;
        double d2 = (double)pos.getZ() + vec3d.z;
        int i = 0;

        for(int j = list.size(); i < j; ++i) {
            BakedQuad bakedquad = list.get(i);
            if (ownBrightness) {
                fillQuadBounds(world, state, pos, bakedquad.getVertexData(), bakedquad.getFace(), (float[])null, bitSet);
                BlockPos blockpos = bitSet.get(0) ? pos.offset(bakedquad.getFace()) : pos;
                brightness = state.getPackedLightmapCoords(world, blockpos);
            }

            buffer.addVertexData(bakedquad.getVertexData());
            buffer.putBrightness4(brightness, brightness, brightness, brightness);
            if (bakedquad.hasTintIndex()) {
                int k = renderer.blockColors.getColor(state, world, pos, bakedquad.getTintIndex());
                float f = (float)(k >> 16 & 255) / 255.0F;
                float f1 = (float)(k >> 8 & 255) / 255.0F;
                float f2 = (float)(k & 255) / 255.0F;
                if(bakedquad.shouldApplyDiffuseLighting()) {
                    float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
                    f *= diffuse;
                    f1 *= diffuse;
                    f2 *= diffuse;
                }
                buffer.putColorMultiplier(f, f1, f2, 4);
                buffer.putColorMultiplier(f, f1, f2, 3);
                buffer.putColorMultiplier(f, f1, f2, 2);
                buffer.putColorMultiplier(f, f1, f2, 1);
            } else if(bakedquad.shouldApplyDiffuseLighting()) {
                float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
                buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
                buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
                buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
                buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
            }

            buffer.putPosition(d0, d1, d2);
        }
    }
}
