package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class VerticalSlabBakedModel extends DynamicSlabBakedModel {

    public static final VerticalSlabBakedModel INSTANCE = new VerticalSlabBakedModel();

    private final Map<IBlockState, IBakedModel> models = new HashMap<>();

    public void addModel(IBakedModel model, IBlockState state) {
        this.models.put(state, model);
    }

    public IBakedModel getModel(IBlockState state) {
        return this.models.get(state);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IBlockInfo positiveBlock = extendedState.getValue(DynamicSlabBlock.POSITIVE_BLOCK);
            IBlockInfo negativeBlock = extendedState.getValue(DynamicSlabBlock.NEGATIVE_BLOCK);

            IBlockState positiveState = positiveBlock.getBlockState();
            IBlockState negativeState = negativeBlock.getBlockState();

            if (positiveState == null && negativeState == null)
                return getFallbackModel().getQuads(state, side, rand);

            boolean positiveTransparent = positiveState == null || ClientConstants.isTransparent(positiveState);
            boolean negativeTransparent = negativeState == null || ClientConstants.isTransparent(negativeState);
            boolean shouldCull = positiveState != null && negativeState != null && DSConfig.CLIENT.shouldCull(positiveState) && DSConfig.CLIENT.shouldCull(negativeState) && (!(positiveTransparent && negativeTransparent) || (positiveState.getBlock() == negativeState.getBlock()));

            boolean renderHalves = false;
            boolean renderPositive = false;

            EnumFacing direction = state.getValue(VerticalSlabBlock.FACING);

            // If the top and bottom states are the same, use the combined block model where possible
            if (positiveState != null && negativeState != null && useDoubleSlabModel(positiveState, negativeState)) {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState);
                if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(positiveState)) {
                    IBlockState doubleState = horizontalSlabSupport.getStateForHalf(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState, null);
                    if (doubleState.getBlock().canRenderInLayer(doubleState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null) {
                        IBakedModel model = ClientConstants.getVerticalModel(doubleState, direction);
                        return model.getQuads(doubleState, side, rand);
                    }
                    return Lists.newArrayList();
                }
            }

            List<BakedQuad> quads = Lists.newArrayList();

            if ((!renderHalves || renderPositive) && positiveState != null && (positiveState.getBlock().canRenderInLayer(positiveState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null)) {
                List<BakedQuad> positiveQuads = getQuadsForState(positiveBlock, ClientConstants.getVerticalModel(positiveState, direction), side, rand);
                if (shouldCull)
                    if ((!negativeTransparent && !positiveTransparent) || (positiveTransparent && !negativeTransparent) || (positiveTransparent && negativeTransparent))
                        positiveQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction.getOpposite());
                quads.addAll(positiveQuads);
            }
            if ((!renderHalves || !renderPositive) && negativeState != null && (negativeState.getBlock().canRenderInLayer(negativeState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null)) {
                List<BakedQuad> negativeQuads = getQuadsForState(negativeBlock, ClientConstants.getVerticalModel(negativeState, direction), side, rand);
                if (shouldCull)
                    if ((!positiveTransparent && !negativeTransparent) || (negativeTransparent && !positiveTransparent) || (positiveTransparent && negativeTransparent))
                        negativeQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction);
                quads.addAll(negativeQuads);
            }
            return quads;
        } else if (MinecraftForgeClient.getRenderLayer() == null) {
            // Rendering the break block animation
            IBakedModel model = this.models.get(state);
            if (model != null)
                return model.getQuads(state, side, rand);
        }
        return getFallbackModel().getQuads(state, side, rand);
    }
}
