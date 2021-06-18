package cjminecraft.doubleslabs.mixin;

import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import cjminecraft.doubleslabs.client.util.AmbientOcclusionFace;
import cjminecraft.doubleslabs.client.util.DoubleSlabCulling;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(BlockModelRenderer.class)
@OnlyIn(Dist.CLIENT)
public abstract class BlockModelRendererMixin {

    protected BlockModelRendererMixin(BlockColors blockColors) {
        this.blockColors = blockColors;
    }

    @Mutable
    @Final
    @Shadow
    private final BlockColors blockColors;

    @Inject(at = @At("HEAD"), remap = false, method = "renderModelSmooth(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z", cancellable = true)
    private void renderModelSmooth(IBlockDisplayReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack matrixStack, IVertexBuilder buffer, boolean checkSides, Random random, long rand, int combinedOverlay, IModelData modelData, CallbackInfoReturnable<Boolean> callback) {
        if (model instanceof DynamicSlabBakedModel) {
            boolean flag = false;
            float[] afloat = new float[Direction.values().length * 2];
            BitSet bitset = new BitSet(3);
            AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new AmbientOcclusionFace();

            boolean doubleSlab = false;

            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) tile;
                doubleSlab = slab.getPositiveBlockInfo().getBlockState() != null && slab.getNegativeBlockInfo().getBlockState() != null && DynamicSlabBakedModel.useDoubleSlabModel(slab.getPositiveBlockInfo().getBlockState(), slab.getNegativeBlockInfo().getBlockState());
            }

            for(Direction direction : Direction.values()) {
                random.setSeed(rand);

                if (doubleSlab) {
                    List<BakedQuad> quads = model.getQuads(state, direction, random, modelData);

                    if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldDoubleSlabSideBeRendered(state, world, pos, direction))) {
                        this.renderQuadsSmooth(world, state, pos, matrixStack, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlay);
                        flag = true;
                    }
                } else {
                    modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, true);
                    List<BakedQuad> quads = model.getQuads(state, direction, random, modelData);

                    if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true))) {
                        this.renderQuadsSmooth(world, state, pos, matrixStack, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlay);
                        flag = true;
                    }

                    modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                    quads = model.getQuads(state, direction, random, modelData);

                    if (!quads.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, false))) {
                        this.renderQuadsSmooth(world, state, pos, matrixStack, buffer, quads, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlay);
                        flag = true;
                    }
                }
            }

            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, null);

            random.setSeed(rand);
            List<BakedQuad> list1 = model.getQuads(state, null, random, modelData);
            if (!list1.isEmpty()) {
                this.renderQuadsSmooth(world, state, pos, matrixStack, buffer, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface, combinedOverlay);
                flag = true;
            }

            callback.setReturnValue(flag);
        }
    }

    @Inject(at = @At("HEAD"), remap = false, method = "renderModelFlat(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z", cancellable = true)
    private void renderModelFlat(IBlockDisplayReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack matrixStack, IVertexBuilder buffer, boolean checkSides, Random random, long rand, int combinedOverlay, IModelData modelData, CallbackInfoReturnable<Boolean> callback) {
        if (model instanceof DynamicSlabBakedModel) {
            boolean flag = false;
            BitSet bitset = new BitSet(3);

            boolean doubleSlab = false;

            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof SlabTileEntity) {
                SlabTileEntity slab = (SlabTileEntity) tile;
                doubleSlab = slab.getPositiveBlockInfo().getBlockState() != null && slab.getNegativeBlockInfo().getBlockState() != null && DynamicSlabBakedModel.useDoubleSlabModel(slab.getPositiveBlockInfo().getBlockState(), slab.getNegativeBlockInfo().getBlockState());
            }

            for(Direction direction : Direction.values()) {
                random.setSeed(rand);

                if (doubleSlab) {
                    List<BakedQuad> list = model.getQuads(state, direction, random, modelData);

                    if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldDoubleSlabSideBeRendered(state, world, pos, direction))) {
                        int i = WorldRenderer.getPackedLightmapCoords(world, state, pos.offset(direction));
                        this.renderQuadsFlat(world, state, pos, i, combinedOverlay, false, matrixStack, buffer, list, bitset);
                        flag = true;
                    }
                } else {
                    modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, true);
                    List<BakedQuad> list = model.getQuads(state, direction, random, modelData);

                    if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, true))) {
                        int i = WorldRenderer.getPackedLightmapCoords(world, state, pos.offset(direction));
                        this.renderQuadsFlat(world, state, pos, i, combinedOverlay, false, matrixStack, buffer, list, bitset);
                        flag = true;
                    }

                    modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, false);
                    list = model.getQuads(state, direction, random, modelData);

                    if (!list.isEmpty() && (!checkSides || DoubleSlabCulling.shouldSideBeRendered(state, world, pos, direction, false))) {
                        int i = WorldRenderer.getPackedLightmapCoords(world, state, pos.offset(direction));
                        this.renderQuadsFlat(world, state, pos, i, combinedOverlay, false, matrixStack, buffer, list, bitset);
                        flag = true;
                    }
                }
            }

            modelData.setData(DynamicSlabBakedModel.RENDER_POSITIVE, null);

            random.setSeed(rand);
            List<BakedQuad> list1 = model.getQuads(state, (Direction)null, random, modelData);
            if (!list1.isEmpty()) {
                this.renderQuadsFlat(world, state, pos, -1, combinedOverlay, true, matrixStack, buffer, list1, bitset);
                flag = true;
            }

            callback.setReturnValue(flag);
        }
    }

    private void renderQuadsSmooth(IBlockDisplayReader blockAccessIn, BlockState stateIn, BlockPos posIn, MatrixStack matrixStackIn, IVertexBuilder buffer, List<BakedQuad> list, float[] quadBounds, BitSet bitSet, AmbientOcclusionFace aoFace, int combinedOverlayIn) {
        Iterator var11 = list.iterator();

        while(var11.hasNext()) {
            BakedQuad bakedquad = (BakedQuad)var11.next();
            this.fillQuadBounds(blockAccessIn, stateIn, posIn, bakedquad.getVertexData(), bakedquad.getFace(), quadBounds, bitSet);
            aoFace.func_239285_a_(blockAccessIn, stateIn, posIn, bakedquad.getFace(), quadBounds, bitSet, bakedquad.func_239287_f_());
            this.renderQuadSmooth(blockAccessIn, stateIn, posIn, buffer, matrixStackIn.getLast(), bakedquad, aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[3], aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3], combinedOverlayIn);
        }

    }

    @Shadow
    private void fillQuadBounds(IBlockDisplayReader blockReaderIn, BlockState stateIn, BlockPos posIn, int[] vertexData, Direction face, @Nullable float[] quadBounds, BitSet boundsFlags) {

    }

    private void renderQuadSmooth(IBlockDisplayReader blockAccessIn, BlockState stateIn, BlockPos posIn, IVertexBuilder buffer, MatrixStack.Entry matrixEntry, BakedQuad quadIn, float colorMul0, float colorMul1, float colorMul2, float colorMul3, int brightness0, int brightness1, int brightness2, int brightness3, int combinedOverlayIn) {
        float f;
        float f1;
        float f2;
        if (quadIn.hasTintIndex()) {
            int i = this.blockColors.getColor(stateIn, blockAccessIn, posIn, quadIn.getTintIndex());
            f = (float)(i >> 16 & 255) / 255.0F;
            f1 = (float)(i >> 8 & 255) / 255.0F;
            f2 = (float)(i & 255) / 255.0F;
        } else {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
        }

        buffer.addQuad(matrixEntry, quadIn, new float[]{colorMul0, colorMul1, colorMul2, colorMul3}, f, f1, f2, new int[]{brightness0, brightness1, brightness2, brightness3}, combinedOverlayIn, true);
    }

    private void renderQuadsFlat(IBlockDisplayReader blockAccessIn, BlockState stateIn, BlockPos posIn, int brightnessIn, int combinedOverlayIn, boolean ownBrightness, MatrixStack matrixStackIn, IVertexBuilder buffer, List<BakedQuad> list, BitSet bitSet) {
        Iterator var11 = list.iterator();

        while(var11.hasNext()) {
            BakedQuad bakedquad = (BakedQuad)var11.next();
            if (ownBrightness) {
                this.fillQuadBounds(blockAccessIn, stateIn, posIn, bakedquad.getVertexData(), bakedquad.getFace(), (float[])null, bitSet);
                BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
                brightnessIn = WorldRenderer.getPackedLightmapCoords(blockAccessIn, stateIn, blockpos);
            }

            float f = blockAccessIn.func_230487_a_(bakedquad.getFace(), bakedquad.func_239287_f_());
            this.renderQuadSmooth(blockAccessIn, stateIn, posIn, buffer, matrixStackIn.getLast(), bakedquad, f, f, f, f, brightnessIn, brightnessIn, brightnessIn, brightnessIn, combinedOverlayIn);
        }
    }
}
