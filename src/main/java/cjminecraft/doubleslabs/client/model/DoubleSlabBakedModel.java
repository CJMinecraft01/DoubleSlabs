package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoubleSlabBakedModel implements IDynamicBakedModel {

    private static IBakedModel fallback;

    private IBakedModel getFallback() {
        if (fallback != null)
            return fallback;
        fallback = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        return fallback;
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
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        if (data.hasProperty(TileEntityDoubleSlab.TOP_STATE) && data.getData(TileEntityDoubleSlab.TOP_STATE) != null)
            return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(data.getData(TileEntityDoubleSlab.TOP_STATE)).getParticleTexture(data);
        return getFallback().getParticleTexture(data);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getFallback().getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getFallback().getOverrides();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(TileEntityDoubleSlab.TOP_STATE) && extraData.hasProperty(TileEntityDoubleSlab.BOTTOM_STATE)) {
            BlockState topState = extraData.getData(TileEntityDoubleSlab.TOP_STATE);
            BlockState bottomState = extraData.getData(TileEntityDoubleSlab.BOTTOM_STATE);
            if (topState == null || bottomState == null)
                return getFallback().getQuads(state, side, rand, extraData);
            IBakedModel topModel = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(topState);
            IBakedModel bottomModel = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(bottomState);
            List<BakedQuad> topQuads = new ArrayList<>(topModel.getQuads(topState, side, rand, extraData));
            boolean topTransparent = !topState.isSolid();
            boolean bottomTransparent = !bottomState.isSolid();
            if (!bottomTransparent)
                topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
            List<BakedQuad> bottomQuads = new ArrayList<>(bottomModel.getQuads(bottomState, side, rand, extraData));
            if (!topTransparent)
                bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
            if (topTransparent && bottomTransparent) {
                topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
                bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
            }
            topQuads.addAll(bottomQuads);
            return topQuads;
        }
        return getFallback().getQuads(state, side, rand, extraData);
    }
}
