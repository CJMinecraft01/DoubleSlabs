package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.CullInfo;
import cjminecraft.doubleslabs.client.util.SlabCacheKey;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public abstract class DynamicSlabBakedModel implements IDynamicBakedModel {

    public static final ModelProperty<IBlockInfo> NEGATIVE_BLOCK = new ModelProperty<>();
    public static final ModelProperty<IBlockInfo> POSITIVE_BLOCK = new ModelProperty<>();
    private static final ModelProperty<List<CullInfo>> CULL_DIRECTIONS = new ModelProperty<>();
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

    protected boolean shouldCull(BlockState state, BlockState neighbour, Direction direction) {
        if (state == null || neighbour == null)
            return false;
        return state.isSideInvisible(neighbour, direction) || (!ClientUtils.isTransparent(state) && !ClientUtils.isTransparent(neighbour));
    }

    protected boolean useDoubleSlabModel(BlockState state1, BlockState state2) {
        return state1.getBlock() == state2.getBlock() && state2.getBlockState().isIn(state2.getBlock()) && DSConfig.CLIENT.useDoubleSlabModel(state1.getBlock());
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(POSITIVE_BLOCK) && extraData.hasProperty(NEGATIVE_BLOCK)) {
            SlabCacheKey key = new SlabCacheKey(extraData.getData(POSITIVE_BLOCK), extraData.getData(NEGATIVE_BLOCK), side, rand, extraData.getData(CULL_DIRECTIONS), extraData, state);
            try {
//                if (false)
//                    throw new ExecutionException("", new Throwable());
//                return getQuads(key);
                return cache.get(key, () -> getQuads(key));
            } catch (ExecutionException e) {
                DoubleSlabs.LOGGER.debug("Caught error when getting quads for key {}", key);
                DoubleSlabs.LOGGER.catching(Level.DEBUG, e);
            }
        } else if (MinecraftForgeClient.getRenderLayer() == null) {
            // Rendering the break block animation
            SlabCacheKey key = new SlabCacheKey(null, null, side, rand, null, extraData, state);
            try {
                return cache.get(key, () -> getQuads(key));
            } catch (ExecutionException e) {
                DoubleSlabs.LOGGER.debug("Caught error when getting quads for key {}", key);
                DoubleSlabs.LOGGER.catching(Level.DEBUG, e);
            }
        }
        return getFallbackModel().getQuads(state, side, rand, extraData);
    }

    protected abstract Block getBlock();

    protected abstract List<BakedQuad> getQuads(SlabCacheKey cache);

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        List<CullInfo> cullDirections = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos otherPos = pos.offset(direction);
            BlockState otherState = world.getBlockState(otherPos);
            if (otherState.getBlock() instanceof DynamicSlabBlock && otherState.getBlock() == getBlock()) {
                TileEntity tileEntity = world.getTileEntity(otherPos);
                if (tileEntity instanceof SlabTileEntity) {
                    SlabTileEntity tile = (SlabTileEntity) tileEntity;
                    cullDirections.add(new CullInfo(tile.getPositiveBlockInfo(), tile.getNegativeBlockInfo(), state, otherState, direction));
                }
            }
        }
        tileData.setData(CULL_DIRECTIONS, cullDirections);
        return tileData;
    }
}
