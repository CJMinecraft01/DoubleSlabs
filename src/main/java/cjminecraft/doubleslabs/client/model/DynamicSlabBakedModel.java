package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.CullInfo;
import cjminecraft.doubleslabs.client.util.SlabCacheKey;
import cjminecraft.doubleslabs.client.util.vertex.TintOffsetTransformer;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.UncheckedExecutionException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
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
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    protected boolean shouldCull(BlockState state, BlockState neighbour, Direction direction) {
        if (state == null || neighbour == null)
            return false;
        return state.isSideInvisible(neighbour, direction) || (!ClientUtils.isTransparent(state) && !ClientUtils.isTransparent(neighbour));
    }

    protected boolean useDoubleSlabModel(BlockState state1, BlockState state2) {
        return state1.getBlock() == state2.getBlock() && state2.getBlockState().isIn(state2.getBlock()) && DSConfig.CLIENT.useDoubleSlabModel(state1.getBlock());
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, Direction side, Random rand) {
        BlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        return getQuadsForState(block, model, side, rand);
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, IBakedModel model, Direction side, Random rand) {
        BlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        IModelData tileData = block.getTileEntity() != null ? block.getTileEntity().getModelData() : EmptyModelData.INSTANCE;
        IModelData modelData = model.getModelData(block.getWorld(), block.getPos(), state, tileData);
        return getQuadsForModel(model, state, side, rand, modelData, block.isPositive());
    }

    protected List<BakedQuad> getQuadsForModel(IBakedModel model, BlockState state, Direction side, Random rand, IModelData modelData, boolean positive) {
        // Ensure blocks have the correct tint
        return model.getQuads(state, side, rand, modelData).stream().map(quad -> {
            BakedQuadBuilder builder = new BakedQuadBuilder();
            TintOffsetTransformer transformer = new TintOffsetTransformer(builder, positive);
            quad.pipe(transformer);
            return builder.build();
        }).collect(Collectors.toList());
    }
}
