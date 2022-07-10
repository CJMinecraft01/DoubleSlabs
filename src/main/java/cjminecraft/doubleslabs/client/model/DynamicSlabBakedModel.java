package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public abstract class DynamicSlabBakedModel implements IDynamicBakedModel {

    public static final ModelProperty<IBlockInfo> NEGATIVE_BLOCK = new ModelProperty<>();
    public static final ModelProperty<IBlockInfo> POSITIVE_BLOCK = new ModelProperty<>();
    public static final ModelProperty<Boolean> RENDER_POSITIVE = new ModelProperty<>();

    @Override
    public boolean isGui3d() {
        return getFallbackModel().isGui3d();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getFallbackModel().getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return getFallbackModel().getOverrides();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull ModelData data) {
        if (data.has(POSITIVE_BLOCK) && data.get(POSITIVE_BLOCK) != null && data.get(POSITIVE_BLOCK).getBlockState() != null)
            return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(data.get(POSITIVE_BLOCK).getBlockState()).getParticleIcon(ModelData.EMPTY);
        return getFallbackModel().getParticleIcon(ModelData.EMPTY);
    }

    protected boolean shouldCull(BlockState state, BlockState neighbour, Direction direction) {
        if (state == null || neighbour == null)
            return false;
        return state.skipRendering(neighbour, direction) || (!ClientConstants.isTransparent(state) && !ClientConstants.isTransparent(neighbour));
    }

    public static boolean useDoubleSlabModel(BlockState state1, BlockState state2) {
        return state1.getBlock() == state2.getBlock() && state2.is(state2.getBlock()) && DSConfig.CLIENT.useDoubleSlabModel(state1.getBlock());
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, Direction side, RandomSource rand, RenderType renderType) {
        BlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (renderType == null || !model.getRenderTypes(state, rand, ModelData.EMPTY).contains(renderType))
            return Lists.newArrayList();
        return getQuadsForState(block, model, side, rand, renderType);
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, BakedModel model, Direction side, RandomSource rand, RenderType renderType) {
        BlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        ModelData tileData = block.getBlockEntity() != null ? block.getBlockEntity().getModelData() : ModelData.EMPTY;
        ModelData modelData = model.getModelData(block.getWorld(), block.getPos(), state, tileData);
        return getQuadsForModel(model, state, side, rand, modelData, renderType, block.isPositive());
    }

    protected List<BakedQuad> getQuadsForModel(BakedModel model, BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType renderType, boolean positive) {
        // Ensure blocks have the correct tint
        return model.getQuads(state, side, rand, modelData, renderType).stream().map(quad ->
                new BakedQuad(quad.getVertices(), quad.isTinted() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getDirection(), quad.getSprite(), quad.isShade())
        ).collect(Collectors.toList());
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ChunkRenderTypeSet.all();
    }
}
