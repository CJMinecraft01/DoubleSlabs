package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.CullInfo;
import cjminecraft.doubleslabs.client.util.SlabCacheKey;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public abstract class DynamicSlabBakedModel implements IBakedModel {

    private final Cache<SlabCacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build();

    @Override
    public boolean isAmbientOcclusion() {
        return getFallbackModel().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return getFallbackModel().isGui3d();
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

    protected boolean shouldCull(IBlockState state, IBlockState neighbour, EnumFacing direction) {
        if (state == null || neighbour == null)
            return false;
//        return state.isSideInvisible(neighbour, direction) || (!ClientUtils.isTransparent(state) && !ClientUtils.isTransparent(neighbour));
        return !ClientUtils.isTransparent(state) && !ClientUtils.isTransparent(neighbour);
    }

    protected boolean useDoubleSlabModel(IBlockState state1, IBlockState state2) {
        return state1 == state2 && DSConfig.CLIENT.useDoubleSlabModel(state1);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
            SlabCacheKey key = new SlabCacheKey(extendedBlockState.getValue(DynamicSlabBlock.POSITIVE_BLOCK), extendedBlockState.getValue(DynamicSlabBlock.NEGATIVE_BLOCK), side, rand, (List<CullInfo>)extendedBlockState.getValue(DynamicSlabBlock.CULL_INFO), state);
            try {
//                if (false)
//                    throw new ExecutionException("", new Throwable());
//                return getQuads(key);
                return cache.get(key, () -> getQuads(key));
            } catch (ExecutionException | UncheckedExecutionException e) {
                DoubleSlabs.LOGGER.debug("Caught error when getting quads for key {}", key);
                DoubleSlabs.LOGGER.catching(Level.DEBUG, e);
            }
        } else if (MinecraftForgeClient.getRenderLayer() == null) {
            // Rendering the break block animation
            SlabCacheKey key = new SlabCacheKey(null, null, side, rand, null, state);
            try {
                return cache.get(key, () -> getQuads(key));
            } catch (ExecutionException | UncheckedExecutionException e) {
                DoubleSlabs.LOGGER.debug("Caught error when getting quads for key {}", key);
                DoubleSlabs.LOGGER.catching(Level.DEBUG, e);
            }
        }
        return getFallbackModel().getQuads(state, side, rand);
    }

    protected abstract Block getBlock();

    protected abstract List<BakedQuad> getQuads(SlabCacheKey cache);

}
