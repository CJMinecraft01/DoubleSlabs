package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.client.util.SlabCache;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public abstract class DynamicSlabBakedModel implements IDynamicBakedModel {

    public static final ModelProperty<IBlockInfo> NEGATIVE_BLOCK = new ModelProperty<>();
    public static final ModelProperty<IBlockInfo> POSITIVE_BLOCK = new ModelProperty<>();

    @Override
    public boolean isAmbientOcclusion() {
        return getFallbackModel().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return getFallbackModel().isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return getFallbackModel().func_230044_c_();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return getFallbackModel().isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getFallbackModel().getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getFallbackModel().getOverrides();
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        if (data.hasProperty(POSITIVE_BLOCK) && data.getData(POSITIVE_BLOCK) != null && data.getData(POSITIVE_BLOCK).getBlockState() != null)
            return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(data.getData(POSITIVE_BLOCK).getBlockState()).getParticleTexture(EmptyModelData.INSTANCE);
        return getFallbackModel().getParticleTexture(EmptyModelData.INSTANCE);
    }

    private static Cache<SlabCache, List<BakedQuad>> cache = CacheBuilder.newBuilder().maximumSize(1000).build();

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(POSITIVE_BLOCK) && extraData.hasProperty(NEGATIVE_BLOCK)) {
            SlabCache key = new SlabCache(extraData.getData(POSITIVE_BLOCK), extraData.getData(NEGATIVE_BLOCK), side, rand);
            try {
                return cache.get(key, () -> getQuads(key));
            } catch (ExecutionException e) {
                DoubleSlabs.LOGGER.debug("Caught error when getting quads for key {}", key);
                DoubleSlabs.LOGGER.catching(Level.DEBUG, e);
            }
        }
        return getFallbackModel().getQuads(state, side, rand, extraData);
    }

    protected abstract List<BakedQuad> getQuads(SlabCache cache);
}
