package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class DoubleSlabBakedModel extends DynamicSlabBakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;

            IBlockInfo positiveBlock = extendedState.getValue(DynamicSlabBlock.POSITIVE_BLOCK);
            IBlockInfo negativeBlock = extendedState.getValue(DynamicSlabBlock.NEGATIVE_BLOCK);

            IBlockState positiveState = positiveBlock.getBlockState();
            IBlockState negativeState = negativeBlock.getBlockState();

            if (positiveState == null || negativeState == null)
                return getFallbackModel().getQuads(state, side, rand);

            // todo add culling
            boolean renderHalves = false;
            boolean renderPositive = false;

            boolean topTransparent = ClientConstants.isTransparent(positiveState);
            boolean bottomTransparent = ClientConstants.isTransparent(negativeState);
            boolean shouldCull = DSConfig.CLIENT.shouldCull(positiveState) && DSConfig.CLIENT.shouldCull(negativeState) && (!(topTransparent && bottomTransparent) || (positiveState.getBlock() == negativeState.getBlock() && positiveState.getBlock() == negativeState.getBlock()));

            // If the top and bottom states are the same, use the combined block model where possible
            if (useDoubleSlabModel(positiveState, negativeState)) {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState);
                if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(positiveState)) {
                    IBlockState doubleState = horizontalSlabSupport.getStateForHalf(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState, null);
                    if (doubleState.getBlock().canRenderInLayer(doubleState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null) {
                        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(doubleState);
                        return model.getQuads(state, side, rand);
                    }
                    return Lists.newArrayList();
                }
            }
            List<BakedQuad> quads = Lists.newArrayList();

            if ((!renderHalves || renderPositive) && (positiveState.getBlock().canRenderInLayer(positiveState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null)) {
                List<BakedQuad> topQuads = getQuadsForState(positiveBlock, side, rand);
                if (shouldCull)
                    if ((!bottomTransparent && !topTransparent) || (topTransparent && !bottomTransparent) || (topTransparent && bottomTransparent))
                        topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.DOWN);
                quads.addAll(topQuads);
            }
            if ((!renderHalves || !renderPositive) && (negativeState.getBlock().canRenderInLayer(negativeState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null)) {
                List<BakedQuad> bottomQuads = getQuadsForState(negativeBlock, side, rand);
                if (shouldCull)
                    if ((!topTransparent && !bottomTransparent) || (bottomTransparent && !topTransparent) || (topTransparent && bottomTransparent))
                        bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.UP);
                quads.addAll(bottomQuads);
            }
            return quads;
        } else if (MinecraftForgeClient.getRenderLayer() == null) {
            // Rendering the break block animation
        }
        return getFallbackModel().getQuads(state, side, rand);
    }
}
