package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class DoubleSlabBakedModel implements IBakedModel {

    private static IBakedModel fallback;

    public static final ModelResourceLocation variantTag
            = new ModelResourceLocation(new ResourceLocation(DoubleSlabs.MODID, "double_slab"), "normal");

    private static IBakedModel getFallback() {
        if (fallback != null)
            return fallback;
        fallback = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        return fallback;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null)
            return getFallback().getQuads(null, side, rand);
        IBlockState topState = ((IExtendedBlockState) state).getValue(BlockDoubleSlab.TOP);
        IBlockState bottomState = ((IExtendedBlockState) state).getValue(BlockDoubleSlab.BOTTOM);
        if (topState == null || bottomState == null)
            return getFallback().getQuads(state, side, rand);
        boolean topTransparent = !topState.isOpaqueCube();
        boolean bottomTransparent = !bottomState.isOpaqueCube();
        IBakedModel topModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(topState);
        IBakedModel bottomModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(bottomState);
        List<BakedQuad> topQuads = new ArrayList<>(topModel.getQuads(topState, side, rand));
        if (!bottomTransparent)
            topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.DOWN);
        List<BakedQuad> bottomQuads = new ArrayList<>(bottomModel.getQuads(bottomState, side, rand));
        if (!topTransparent)
            bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.UP);
        if (topTransparent && bottomTransparent) {
            topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.DOWN);
            bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.UP);
        }
        topQuads.addAll(bottomQuads);
        return topQuads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return getFallback().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return getFallback().isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return getFallback().isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getFallback().getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getFallback().getOverrides();
    }
}
