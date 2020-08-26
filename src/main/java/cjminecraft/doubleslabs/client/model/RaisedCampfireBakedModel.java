package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.util.CacheKey;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class RaisedCampfireBakedModel implements IDynamicBakedModel {

    private final Cache<CacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().maximumSize(8).build();

    private IBakedModel baseModel;
    private final Map<BlockState, IBakedModel> models = new HashMap<>();

    public void addModel(IBakedModel model, BlockState state) {
        if (this.models.size() == 0)
            this.baseModel = model;
        this.models.put(state, model);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (state == null)
            return this.baseModel.getQuads(null, side, rand, extraData);
        try {
            CacheKey cacheKey = new CacheKey(state, side, rand, extraData);
            if (RenderTypeLookup.canRenderInLayer(state, cacheKey.getRenderLayer()) || cacheKey.getRenderLayer() == null)
                return cache.get(cacheKey, () ->
                    models.get(cacheKey.getState()).getQuads(cacheKey.getState(), cacheKey.getSide(), cacheKey.getRandom(), cacheKey.getModelData())
                    .stream().map(quad ->
                            new BakedQuad(ClientUtils.offsetY(quad.getVertexData(), 0.5f),
                                    quad.hasTintIndex() ? quad.getTintIndex() : -1, quad.getFace(),
                                    quad.func_187508_a(), quad.func_239287_f_())).collect(Collectors.toList()));
            return new ArrayList<>();
//                    .stream().map(quad ->
//                    new BakedQuad(ClientUtils.offsetY(quad.getVertexData(), 0.5f),
//                            quad.hasTintIndex() ? quad.getTintIndex() : -1, quad.getFace(),
//                            quad.func_187508_a(), quad.func_239287_f_())).collect(Collectors.toList());
//            return cache.get(state, () ->
//                    models.get(state).getQuads(state, side, rand, extraData)
//                    .stream().map(quad ->
//                            new BakedQuad(ClientUtils.offsetY(quad.getVertexData(), 0.5f),
//                                    quad.hasTintIndex() ? quad.getTintIndex() : -1, quad.getFace(),
//                                    quad.func_187508_a(), quad.func_239287_f_())).collect(Collectors.toList());
        } catch (ExecutionException e) {
            DoubleSlabs.LOGGER.debug("Caught error when getting quads for key {}", state);
            DoubleSlabs.LOGGER.catching(Level.DEBUG, e);
        }
        return this.baseModel.getQuads(state, side, rand, extraData);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.baseModel.isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return this.baseModel.func_230044_c_();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.baseModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.baseModel.getOverrides();
    }
}
