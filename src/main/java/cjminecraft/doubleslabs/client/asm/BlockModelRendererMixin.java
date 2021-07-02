package cjminecraft.doubleslabs.client.asm;

import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.client.util.AmbientOcclusionFace;
import cjminecraft.doubleslabs.client.util.DoubleSlabCulling;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class BlockModelRendererMixin {

    private static <T> IBlockState setValue(IBlockState state, IUnlistedProperty<T> property, T value) {
        if (state instanceof IExtendedBlockState) {
            return ((IExtendedBlockState) state).withProperty(property, value);
        }
        return state;
    }

    public static boolean renderModelSmooth(BlockModelRenderer renderer, IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, long rand) {
        if (model instanceof DynamicSlabBakedModel) {
            boolean flag = false;
            float[] afloat = new float[EnumFacing.values().length * 2];
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

            for(EnumFacing direction : EnumFacing.values()) {
                if (doubleSlab) {
                    List<BakedQuad> quads = model.getQuads(state, direction, rand);

                    if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldDoubleSlabSideBeRendered(state, world, pos, direction))) {
                        renderQuadsSmooth(renderer, world, state, pos, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                        flag = true;
                    }
                } else {
                    if (renderPositive) {
                        state = setValue(state, DynamicSlabBlock.RENDER_POSITIVE, true);
                        List<BakedQuad> quads = model.getQuads(state, direction, rand);

                        if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true))) {
                            renderQuadsSmooth(renderer, world, state, pos, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                            flag = true;
                        }
                    }
                    if (renderNegative) {
                        List<BakedQuad> quads;
                        if (MinecraftForgeClient.getRenderLayer() == null && model instanceof VerticalSlabBakedModel) {
                            // Handle the block breaking animation for a single vertical slab on the negative half
                            // We must flip the EnumFacing of the facing in order to get the correct half rendered
                            IBlockState newState = state.withProperty(VerticalSlabBlock.FACING, state.getValue(VerticalSlabBlock.FACING).getOpposite());
                            quads = ((VerticalSlabBakedModel) model).getModel(newState).getQuads(newState, direction, rand);
                        } else {
                            state = setValue(state, DynamicSlabBlock.RENDER_POSITIVE, false);
                            quads = model.getQuads(state, direction, rand);
                        }

                        if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, false))) {
                            renderQuadsSmooth(renderer, world, state, pos, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                            flag = true;
                        }
                    }
                }
            }

            state = setValue(state, DynamicSlabBlock.RENDER_POSITIVE, null);
            
            List<BakedQuad> list1 = model.getQuads(state, null, rand);
            if (!list1.isEmpty()) {
                renderQuadsSmooth(renderer, world, state, pos, buffer, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                flag = true;
            }

            return flag;
        }
        return false;
    }

    public static boolean renderModelFlat(BlockModelRenderer renderer, IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, long rand) {
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

            for(EnumFacing direction : EnumFacing.values()) {
                if (doubleSlab) {
                    List<BakedQuad> list = model.getQuads(state, direction, rand);

                    if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldDoubleSlabSideBeRendered(state, world, pos, direction))) {
                        int i = state.getPackedLightmapCoords(world, pos.offset(direction));
                        renderQuadsFlat(renderer, world, state, pos, i, false, buffer, list, bitset);
                        flag = true;
                    }
                } else {
                    if (renderPositive) {
                        state = setValue(state, DynamicSlabBlock.RENDER_POSITIVE, true);
                        List<BakedQuad> list = model.getQuads(state, direction, rand);

                        if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true))) {
                            int i = state.getPackedLightmapCoords(world, pos.offset(direction));
                            renderQuadsFlat(renderer, world, state, pos, i, false, buffer, list, bitset);
                            flag = true;
                        }
                    }

                    if (renderNegative) {
//                        modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                        List<BakedQuad> list;
                        if (MinecraftForgeClient.getRenderLayer() == null && model instanceof VerticalSlabBakedModel) {
                            // Handle the block breaking animation for a single vertical slab on the negative half
                            // We must flip the EnumFacing of the facing in order to get the correct half rendered
                            IBlockState newState = state.withProperty(VerticalSlabBlock.FACING, state.getValue(VerticalSlabBlock.FACING).getOpposite());
                            list = ((VerticalSlabBakedModel) model).getModel(newState).getQuads(newState, direction, rand);
                        } else {
                            state = setValue(state, DynamicSlabBlock.RENDER_POSITIVE, false);
                            list = model.getQuads(state, direction, rand);
                        }

                        if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, false))) {
                            int i = state.getPackedLightmapCoords(world, pos.offset(direction));
                            renderQuadsFlat(renderer, world, state, pos, i, false, buffer, list, bitset);
                            flag = true;
                        }
                    }
                }
            }

            state = setValue(state, DynamicSlabBlock.RENDER_POSITIVE, null);
            
            List<BakedQuad> list1 = model.getQuads(state, (EnumFacing) null, rand);
            if (!list1.isEmpty()) {
                renderQuadsFlat(renderer, world, state, pos, -1, true, buffer, list1, bitset);
                flag = true;
            }

            return flag;
        }
        return false;
    }

    private static void fillQuadBounds(IBlockState stateIn, int[] vertexData, EnumFacing face, @Nullable float[] quadBounds, BitSet boundsFlags) {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        for (int i = 0; i < 4; ++i)
        {
            float f6 = Float.intBitsToFloat(vertexData[i * 7]);
            float f7 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
            float f8 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (quadBounds != null)
        {
            quadBounds[EnumFacing.WEST.getIndex()] = f;
            quadBounds[EnumFacing.EAST.getIndex()] = f3;
            quadBounds[EnumFacing.DOWN.getIndex()] = f1;
            quadBounds[EnumFacing.UP.getIndex()] = f4;
            quadBounds[EnumFacing.NORTH.getIndex()] = f2;
            quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
            int j = EnumFacing.values().length;
            quadBounds[EnumFacing.WEST.getIndex() + j] = 1.0F - f;
            quadBounds[EnumFacing.EAST.getIndex() + j] = 1.0F - f3;
            quadBounds[EnumFacing.DOWN.getIndex() + j] = 1.0F - f1;
            quadBounds[EnumFacing.UP.getIndex() + j] = 1.0F - f4;
            quadBounds[EnumFacing.NORTH.getIndex() + j] = 1.0F - f2;
            quadBounds[EnumFacing.SOUTH.getIndex() + j] = 1.0F - f5;
        }

        float f9 = 1.0E-4F;
        float f10 = 0.9999F;

        switch (face)
        {
            case DOWN:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f1 < 1.0E-4F || stateIn.isFullCube()) && f1 == f4);
                break;
            case UP:
                boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f4 > 0.9999F || stateIn.isFullCube()) && f1 == f4);
                break;
            case NORTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f2 < 1.0E-4F || stateIn.isFullCube()) && f2 == f5);
                break;
            case SOUTH:
                boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                boundsFlags.set(0, (f5 > 0.9999F || stateIn.isFullCube()) && f2 == f5);
                break;
            case WEST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f < 1.0E-4F || stateIn.isFullCube()) && f == f3);
                break;
            case EAST:
                boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                boundsFlags.set(0, (f3 > 0.9999F || stateIn.isFullCube()) && f == f3);
        }
    }

    private static void renderQuadsSmooth(BlockModelRenderer renderer, IBlockAccess blockAccessIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, List<BakedQuad> list, float[] quadBounds, BitSet bitSet, AmbientOcclusionFace aoFace) {
        Vec3d vec3d = stateIn.getOffset(blockAccessIn, posIn);
        double d0 = (double)posIn.getX() + vec3d.x;
        double d1 = (double)posIn.getY() + vec3d.y;
        double d2 = (double)posIn.getZ() + vec3d.z;
        int i = 0;

        for (int j = list.size(); i < j; ++i)
        {
            BakedQuad bakedquad = list.get(i);
            fillQuadBounds(stateIn, bakedquad.getVertexData(), bakedquad.getFace(), quadBounds, bitSet);
            aoFace.updateVertexBrightness(blockAccessIn, stateIn, posIn, bakedquad.getFace(), quadBounds, bitSet);
            buffer.addVertexData(bakedquad.getVertexData());
            buffer.putBrightness4(aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3]);
            if(bakedquad.shouldApplyDiffuseLighting())
            {
                float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
                aoFace.vertexColorMultiplier[0] *= diffuse;
                aoFace.vertexColorMultiplier[1] *= diffuse;
                aoFace.vertexColorMultiplier[2] *= diffuse;
                aoFace.vertexColorMultiplier[3] *= diffuse;
            }
            if (bakedquad.hasTintIndex())
            {
                int k = renderer.blockColors.colorMultiplier(stateIn, blockAccessIn, posIn, bakedquad.getTintIndex());

                if (EntityRenderer.anaglyphEnable)
                {
                    k = TextureUtil.anaglyphColor(k);
                }

                float f = (float)(k >> 16 & 255) / 255.0F;
                float f1 = (float)(k >> 8 & 255) / 255.0F;
                float f2 = (float)(k & 255) / 255.0F;
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[0] * f, aoFace.vertexColorMultiplier[0] * f1, aoFace.vertexColorMultiplier[0] * f2, 4);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[1] * f, aoFace.vertexColorMultiplier[1] * f1, aoFace.vertexColorMultiplier[1] * f2, 3);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[2] * f, aoFace.vertexColorMultiplier[2] * f1, aoFace.vertexColorMultiplier[2] * f2, 2);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[3] * f, aoFace.vertexColorMultiplier[3] * f1, aoFace.vertexColorMultiplier[3] * f2, 1);
            }
            else
            {
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], 4);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], 3);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], 2);
                buffer.putColorMultiplier(aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], 1);
            }

            buffer.putPosition(d0, d1, d2);
        }
    }

    private static void renderQuadsFlat(BlockModelRenderer renderer, IBlockAccess blockAccessIn, IBlockState stateIn, BlockPos posIn, int brightnessIn, boolean ownBrightness, BufferBuilder buffer, List<BakedQuad> list, BitSet bitSet) {
        Vec3d vec3d = stateIn.getOffset(blockAccessIn, posIn);
        double d0 = (double)posIn.getX() + vec3d.x;
        double d1 = (double)posIn.getY() + vec3d.y;
        double d2 = (double)posIn.getZ() + vec3d.z;
        int i = 0;

        for (int j = list.size(); i < j; ++i)
        {
            BakedQuad bakedquad = list.get(i);

            if (ownBrightness)
            {
                fillQuadBounds(stateIn, bakedquad.getVertexData(), bakedquad.getFace(), (float[])null, bitSet);
                BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
                brightnessIn = stateIn.getPackedLightmapCoords(blockAccessIn, blockpos);
            }

            buffer.addVertexData(bakedquad.getVertexData());
            buffer.putBrightness4(brightnessIn, brightnessIn, brightnessIn, brightnessIn);

            if (bakedquad.hasTintIndex())
            {
                int k = renderer.blockColors.colorMultiplier(stateIn, blockAccessIn, posIn, bakedquad.getTintIndex());

                if (EntityRenderer.anaglyphEnable)
                {
                    k = TextureUtil.anaglyphColor(k);
                }

                float f = (float)(k >> 16 & 255) / 255.0F;
                float f1 = (float)(k >> 8 & 255) / 255.0F;
                float f2 = (float)(k & 255) / 255.0F;
                if(bakedquad.shouldApplyDiffuseLighting())
                {
                    float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
                    f *= diffuse;
                    f1 *= diffuse;
                    f2 *= diffuse;
                }
                buffer.putColorMultiplier(f, f1, f2, 4);
                buffer.putColorMultiplier(f, f1, f2, 3);
                buffer.putColorMultiplier(f, f1, f2, 2);
                buffer.putColorMultiplier(f, f1, f2, 1);
            }
            else if(bakedquad.shouldApplyDiffuseLighting())
            {
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
