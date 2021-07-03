package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

@SideOnly(Side.CLIENT)
public abstract class DynamicSlabBakedModel implements IBakedModel {

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
    	try
    	{
        return getFallbackModel().getOverrides();
    	}
    	catch(Exception e)
    	{
    	    return null;
    	}
    }

    protected boolean shouldCull(IBlockState state, IBlockState neighbour, EnumFacing direction) {
        if (state == null || neighbour == null)
            return false;
//        return state.isSideInvisible(neighbour, direction) || (!ClientUtils.isTransparent(state) && !ClientUtils.isTransparent(neighbour));
        return !ClientConstants.isTransparent(state) && !ClientConstants.isTransparent(neighbour);
    }

    public static boolean useDoubleSlabModel(IBlockState state1, IBlockState state2) {
        return state1.equals(state2) && DSConfig.CLIENT.useDoubleSlabModel(state1);
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, EnumFacing side, long rand) {
        IBlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        return getQuadsForState(block, model, side, rand);
    }

    protected List<BakedQuad> getQuadsForState(IBlockInfo block, IBakedModel model, EnumFacing side, long rand) {
        IBlockState state = block.getBlockState();
        if (state == null)
            return Lists.newArrayList();
        return getQuadsForModel(model, state, side, rand, block.isPositive());
    }

    protected List<BakedQuad> getQuadsForModel(IBakedModel model, IBlockState state, EnumFacing side, long rand, boolean positive) {
        // Ensure blocks have the correct tint
        return model.getQuads(state, side, rand).stream().map(quad ->
                new BakedQuad(quad.getVertexData(), quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat())
        ).collect(Collectors.toList());
    }

}
