package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.Config;
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
import java.util.*;
import java.util.stream.Collectors;

public class DoubleSlabBakedModel implements IDynamicBakedModel {

    private final Map<String, List<BakedQuad>> cache = new HashMap<>();
    // Should be large enough so that tint offsets don't overlap
    public static final int TINT_OFFSET = 1000;

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

    private List<BakedQuad> getQuadsForState(@Nullable BlockState state, @Nullable Direction side, Random rand, @Nonnull IModelData extraData, int tintOffset) {
        if (state == null) return new ArrayList<>();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        return model.getQuads(state, side, rand, extraData).stream().map(quad -> new BakedQuad(quad.getVertexData(), quad.hasTintIndex() ? quad.getTintIndex() + tintOffset : -1, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat())).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(TileEntityDoubleSlab.TOP_STATE) && extraData.hasProperty(TileEntityDoubleSlab.BOTTOM_STATE)) {
            BlockState topState = extraData.getData(TileEntityDoubleSlab.TOP_STATE);
            BlockState bottomState = extraData.getData(TileEntityDoubleSlab.BOTTOM_STATE);
            String cacheKey = Config.slabToString(topState) + "," + Config.slabToString(bottomState) + ":" + (side != null ? side.getName() : "null");
            if (!cache.containsKey(cacheKey)) {
                if (topState == null || bottomState == null) {
                    List<BakedQuad> quads = getFallback().getQuads(null, side, rand);
                    cache.put(cacheKey, quads);
                    return quads;
                }
                boolean topTransparent = !topState.isSolid();
                boolean bottomTransparent = !bottomState.isSolid();

                List<BakedQuad> topQuads = getQuadsForState(topState, side, rand, extraData, 0);
                if (!bottomTransparent)
                    topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
                List<BakedQuad> bottomQuads = getQuadsForState(bottomState, side, rand, extraData, TINT_OFFSET);
                if (!topTransparent)
                    bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
                if (topTransparent && bottomTransparent) {
                    topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
                    bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
                }
                topQuads.addAll(bottomQuads);
                cache.put(cacheKey, topQuads);
                return topQuads;
            } else {
                return cache.get(cacheKey);
            }
        }
        return getFallback().getQuads(state, side, rand, extraData);
    }
}
