package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
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
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) {
        if (data.hasProperty(POSITIVE_BLOCK) && data.getData(POSITIVE_BLOCK) != null && data.getData(POSITIVE_BLOCK).getBlockState() != null)
            return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(data.getData(POSITIVE_BLOCK).getBlockState()).getParticleIcon(EmptyModelData.INSTANCE);
        return getFallbackModel().getParticleIcon(EmptyModelData.INSTANCE);
    }

    protected boolean shouldCull(BlockState state, BlockState neighbour, Direction direction) {
        if (state == null || neighbour == null)
            return false;
        return state.skipRendering(neighbour, direction) || (!ClientConstants.isTransparent(state) && !ClientConstants.isTransparent(neighbour));
    }

    public static boolean useDoubleSlabModel(BlockState state1, BlockState state2) {
        return state1.getBlock() == state2.getBlock() && state2.is(state2.getBlock()) && DSConfig.CLIENT.useDoubleSlabModel(state1.getBlock());
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, Direction side, Random rand) {
        BlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        return getQuadsForState(block, model, side, rand);
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, BakedModel model, Direction side, Random rand) {
        BlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        IModelData tileData = block.getBlockEntity() != null ? block.getBlockEntity().getModelData() : EmptyModelData.INSTANCE;
        IModelData modelData = model.getModelData(block.getWorld(), block.getPos(), state, tileData);
        return getQuadsForModel(model, state, side, rand, modelData, block.isPositive());
    }

    protected List<BakedQuad> getQuadsForModel(BakedModel model, BlockState state, Direction side, Random rand, IModelData modelData, boolean positive) {
        // Ensure blocks have the correct tint
        return model.getQuads(state, side, rand, modelData).stream().map(quad ->
                new BakedQuad(quad.getVertices(), quad.isTinted() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getDirection(), quad.getSprite(), quad.isShade())
        ).collect(Collectors.toList());
    }
}
