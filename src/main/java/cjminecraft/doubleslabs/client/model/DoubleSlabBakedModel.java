package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.Config;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class DoubleSlabBakedModel implements IDynamicBakedModel {

    public static final ModelProperty<Float> OFFSET_Y_POSITIVE = new ModelProperty<>();
    public static final ModelProperty<Float> OFFSET_Y_NEGATIVE = new ModelProperty<>();

    private final Map<String, List<BakedQuad>> cache = new HashMap<>();
    // Should be large enough so that tint offsets don't overlap
    public static final int TINT_OFFSET = 1000;

    private static IBakedModel fallback;

    protected IBakedModel getFallback() {
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
    public boolean func_230044_c_() {
        return getFallback().func_230044_c_();
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

    protected static int[] offsetY(int[] vertexData, float amount) {
        int[] data = new int[vertexData.length];
        for (int i = 0; i < vertexData.length / 8; i++) {
            data[i * 8] = vertexData[i * 8];
            data[i * 8 + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(vertexData[i * 8 + 1]) + amount);
            data[i * 8 + 2] = vertexData[i * 8 + 2];
            data[i * 8 + 3] = vertexData[i * 8 + 3]; // shade colour
            data[i * 8 + 4] = vertexData[i * 8 + 4]; // texture U
            data[i * 8 + 5] = vertexData[i * 8 + 5]; // texture V
            data[i * 8 + 6] = vertexData[i * 8 + 6]; // baked lighting
            data[i * 8 + 7] = vertexData[i * 8 + 7]; // normal
        }
        return data;
    }

    private List<BakedQuad> getQuadsForState(@Nullable BlockState state, @Nullable Direction side, Random rand, @Nonnull IModelData extraData, int tintOffset, boolean positive) {
        if (state == null) return new ArrayList<>();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        return model.getQuads(state, side, rand, extraData).stream().map(quad -> new BakedQuad(positive && extraData.getData(OFFSET_Y_POSITIVE) != 0 ? offsetY(quad.getVertexData(), extraData.getData(OFFSET_Y_POSITIVE)): !positive && extraData.getData(OFFSET_Y_NEGATIVE) != 0 ? offsetY(quad.getVertexData(), extraData.getData(OFFSET_Y_NEGATIVE)) : quad.getVertexData(), quad.hasTintIndex() ? quad.getTintIndex() + tintOffset : -1, quad.getFace(), quad.func_187508_a(), quad.shouldApplyDiffuseLighting())).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(TileEntityDoubleSlab.TOP_STATE) && extraData.hasProperty(TileEntityDoubleSlab.BOTTOM_STATE)) {
            BlockState topState = extraData.getData(TileEntityDoubleSlab.TOP_STATE);
            BlockState bottomState = extraData.getData(TileEntityDoubleSlab.BOTTOM_STATE);
            String cacheKey = (bottomState != null ? bottomState.toString() : "null") + "," + (topState != null ? topState.toString() : "null") +
                    ":" + (side != null ? side.getName() : "null") + ":" +
                    (MinecraftForgeClient.getRenderLayer() != null ? MinecraftForgeClient.getRenderLayer().toString() : "null");
            if (!cache.containsKey(cacheKey)) {
                if (topState == null || bottomState == null)
                    //                    cache.put(cacheKey, quads);
                    return getFallback().getQuads(null, side, rand, extraData);
                boolean shouldCull = Config.shouldCull(topState.getBlock()) && Config.shouldCull(bottomState.getBlock());
                boolean topTransparent = Utils.isTransparent(topState);
                boolean bottomTransparent = Utils.isTransparent(bottomState);

                List<BakedQuad> quads = new ArrayList<>();
                if (RenderTypeLookup.canRenderInLayer(topState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null) {
                    List<BakedQuad> topQuads = getQuadsForState(topState, side, rand, extraData, 0, true);
                    if (shouldCull)
                        if ((!bottomTransparent && !topTransparent) || (topTransparent && !bottomTransparent) || (topTransparent && bottomTransparent))
                            topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
                    quads.addAll(topQuads);
                }
                if (RenderTypeLookup.canRenderInLayer(bottomState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null) {
                    List<BakedQuad> bottomQuads = getQuadsForState(bottomState, side, rand, extraData, TINT_OFFSET, false);
                    if (shouldCull)
                        if ((!topTransparent && !bottomTransparent) || (bottomTransparent && !topTransparent) || (topTransparent && bottomTransparent))
                            bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
                    quads.addAll(bottomQuads);
                }

                cache.put(cacheKey, quads);
                return quads;
            } else {
                return cache.get(cacheKey);
            }
        }
        return getFallback().getQuads(state, side, rand, extraData);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        float offsetYPositive = 0;
        float offsetYNegative = 0;
        if (tileData.getData(TileEntityDoubleSlab.TOP_STATE) != null) {
            ISlabSupport positiveSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, tileData.getData(TileEntityDoubleSlab.TOP_STATE));
            if (positiveSlabSupport != null)
                offsetYPositive = positiveSlabSupport.getOffsetY(true);
        }
        if (tileData.getData(TileEntityDoubleSlab.BOTTOM_STATE) != null) {
            ISlabSupport negativeSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, tileData.getData(TileEntityDoubleSlab.BOTTOM_STATE));
            if (negativeSlabSupport != null)
                offsetYNegative = negativeSlabSupport.getOffsetY(false);
        }
        tileData.setData(OFFSET_Y_POSITIVE, offsetYPositive);
        tileData.setData(OFFSET_Y_NEGATIVE, offsetYNegative);
        return tileData;
    }
}
