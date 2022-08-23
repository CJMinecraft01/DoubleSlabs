package cjminecraft.doubleslabs.forge.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
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
import net.minecraft.world.level.block.state.properties.SlabType;
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
        return getQuadsForState(block, model, side, rand, renderType);
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, BakedModel model, Direction side, RandomSource rand, RenderType renderType) {
        BlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        if (renderType != null && !model.getRenderTypes(state, rand, ModelData.EMPTY).contains(renderType))
            return Lists.newArrayList();
        ModelData tileData = block.getBlockEntity() != null ? block.getBlockEntity().getModelData() : ModelData.EMPTY;
        ModelData modelData = model.getModelData(block.getLevel(), block.getPos(), state, tileData);
        return getQuadsForModel(model, state, side, rand, modelData, renderType, block.isPositive());
    }

    protected List<BakedQuad> getQuadsForModel(BakedModel model, BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType renderType, boolean positive) {
        // Ensure blocks have the correct tint
        return model.getQuads(state, side, rand, modelData, renderType).stream().map(quad ->
                new BakedQuad(quad.getVertices(), quad.isTinted() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getDirection(), quad.getSprite(), quad.isShade())
        ).collect(Collectors.toList());
    }

    private ChunkRenderTypeSet getRenderTypes(IBlockInfo block, @NotNull RandomSource rand) {
        BlockState state = block.getBlockState();
        if (state == null)
            return ChunkRenderTypeSet.none();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        ModelData tileData = block.getBlockEntity() != null ? block.getBlockEntity().getModelData() : ModelData.EMPTY;
        ModelData modelData = model.getModelData(block.getLevel(), block.getPos(), state, tileData);
        return model.getRenderTypes(state, rand, modelData);
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        if (data.has(POSITIVE_BLOCK) && data.has(NEGATIVE_BLOCK)) {
            IBlockInfo positiveBlock = data.get(POSITIVE_BLOCK);
            IBlockInfo negativeBlock = data.get(NEGATIVE_BLOCK);
            assert positiveBlock != null;
            assert negativeBlock != null;
            BlockState positiveState = positiveBlock.getBlockState();
            BlockState negativeState = negativeBlock.getBlockState();
            // todo: there is an issue when we have double slabs of the same type
            if (positiveState != null && negativeState != null) {
                if (useDoubleSlabModel(positiveState, negativeState)) {
                    IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(positiveBlock.getLevel(), positiveBlock.getPos(), positiveState);
                    if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(positiveState)) {
                        BlockState doubleState = horizontalSlabSupport.getStateForHalf(positiveBlock.getLevel(), positiveBlock.getPos(), positiveState, SlabType.DOUBLE);
                        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(doubleState);
                        return model.getRenderTypes(doubleState, rand, ModelData.EMPTY);
                    }
                }
            }
            // this should be union
            return ChunkRenderTypeSet.union(getRenderTypes(positiveBlock, rand), getRenderTypes(negativeBlock, rand));
        }
        return ChunkRenderTypeSet.all();
    }
}