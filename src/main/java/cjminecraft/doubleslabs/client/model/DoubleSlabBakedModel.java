package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.DoubleSlabsConfig;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class DoubleSlabBakedModel implements IBakedModel {

    // Should be large enough so that tint offsets don't overlap
    public static final int TINT_OFFSET = 1000;
    public static final ModelResourceLocation variantTag
            = new ModelResourceLocation(new ResourceLocation(DoubleSlabs.MODID, "double_slab"), "normal");
    private static IBakedModel fallback;
    private final Map<String, List<BakedQuad>> cache = new HashMap<>();

    private static IBakedModel getFallback() {
        if (fallback != null)
            return fallback;
        fallback = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        return fallback;
    }

    private List<BakedQuad> getQuadsForState(@Nullable IBlockState state, @Nullable EnumFacing side, long rand, int tintOffset) {
        if (state == null) return new ArrayList<>();
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        return model.getQuads(state, side, rand).stream().map(quad -> new BakedQuad(quad.getVertexData(), quad.hasTintIndex() ? quad.getTintIndex() + tintOffset : -1, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat())).collect(Collectors.toList());
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null)
            return getFallback().getQuads(null, side, rand);
        IBlockState topState = ((IExtendedBlockState) state).getValue(BlockDoubleSlab.TOP);
        IBlockState bottomState = ((IExtendedBlockState) state).getValue(BlockDoubleSlab.BOTTOM);
        String cacheKey = DoubleSlabsConfig.slabToString(topState) + "," + DoubleSlabsConfig.slabToString(bottomState)
                + ":" + (side != null ? side.getName() : "null") + ":" +
                (MinecraftForgeClient.getRenderLayer() != null ? MinecraftForgeClient.getRenderLayer().toString() : "null");
        if (!cache.containsKey(cacheKey)) {
            if (topState == null || bottomState == null) {
                List<BakedQuad> quads = getFallback().getQuads(null, side, rand);
                cache.put(cacheKey, quads);
                return quads;
            }
            boolean topTransparent = Utils.isTransparent(topState);
            boolean bottomTransparent = Utils.isTransparent(bottomState);

//            DoubleSlabs.LOGGER.info(topState);
//            DoubleSlabs.LOGGER.info(bottomState);
//            DoubleSlabs.LOGGER.info(topTransparent);
//            DoubleSlabs.LOGGER.info(bottomTransparent);
//            DoubleSlabs.LOGGER.info("===============");

            List<BakedQuad> quads = new ArrayList<>();
            if (MinecraftForgeClient.getRenderLayer() == topState.getBlock().getRenderLayer()) {
                List<BakedQuad> topQuads = getQuadsForState(topState, side, rand, 0);
                if ((!bottomTransparent && !topTransparent) || (topTransparent && !bottomTransparent) || (topTransparent && bottomTransparent))
                    topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.DOWN);
                quads.addAll(topQuads);
            }
            if (MinecraftForgeClient.getRenderLayer() == bottomState.getBlock().getRenderLayer()) {
                List<BakedQuad> bottomQuads = getQuadsForState(bottomState, side, rand, TINT_OFFSET);
                if ((!topTransparent && !bottomTransparent) || (bottomTransparent && !topTransparent) || (topTransparent && bottomTransparent))
                    bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.UP);
                quads.addAll(bottomQuads);
            }

            cache.put(cacheKey, quads);
            return quads;
        } else {
            return cache.get(cacheKey);
        }
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
