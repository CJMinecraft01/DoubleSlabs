package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class DoubleSlabBakedModel extends DynamicSlabBakedModel {

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(POSITIVE_BLOCK) && extraData.hasProperty(NEGATIVE_BLOCK)) {
            IBlockInfo positiveBlock = extraData.getData(POSITIVE_BLOCK);
            IBlockInfo negativeBlock = extraData.getData(NEGATIVE_BLOCK);

            assert positiveBlock != null;
            assert negativeBlock != null;

            BlockState positiveState = positiveBlock.getBlockState();
            BlockState negativeState = negativeBlock.getBlockState();

            if (positiveState == null || negativeState == null)
                return getFallbackModel().getQuads(state, side, rand, extraData);

            boolean renderHalves = extraData.hasProperty(RENDER_POSITIVE) && extraData.getData(RENDER_POSITIVE) != null;
            boolean renderPositive = renderHalves && extraData.getData(RENDER_POSITIVE);

            boolean topTransparent = ClientConstants.isTransparent(positiveState);
            boolean bottomTransparent = ClientConstants.isTransparent(negativeState);
            boolean shouldCull = DSConfig.CLIENT.shouldCull(positiveState.getBlock()) && DSConfig.CLIENT.shouldCull(negativeState.getBlock()) && (!(topTransparent && bottomTransparent) || (positiveState.getBlock() == negativeState.getBlock() && positiveState.is(negativeState.getBlock())));

            // If the top and bottom states are the same, use the combined block model where possible
            if (useDoubleSlabModel(positiveState, negativeState)) {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState);
                if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(positiveState)) {
                    BlockState doubleState = horizontalSlabSupport.getStateForHalf(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState, SlabType.DOUBLE);
                    if (ItemBlockRenderTypes.canRenderInLayer(doubleState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null) {
                        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(doubleState);
                        return model.getQuads(doubleState, side, rand, EmptyModelData.INSTANCE);
                    }
                    return Lists.newArrayList();
                }
            }

            List<BakedQuad> quads = Lists.newArrayList();

            if ((!renderHalves || renderPositive) && (ItemBlockRenderTypes.canRenderInLayer(positiveState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null)) {
                List<BakedQuad> topQuads = getQuadsForState(positiveBlock, side, rand);
                if (shouldCull)
                    if ((!bottomTransparent && !topTransparent) || (topTransparent && !bottomTransparent) || (topTransparent && bottomTransparent))
                        topQuads.removeIf(bakedQuad -> bakedQuad.getDirection() == Direction.DOWN);
                quads.addAll(topQuads);
            }
            if ((!renderHalves || !renderPositive) && (ItemBlockRenderTypes.canRenderInLayer(negativeState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null)) {
                List<BakedQuad> bottomQuads = getQuadsForState(negativeBlock, side, rand);
                if (shouldCull)
                    if ((!topTransparent && !bottomTransparent) || (bottomTransparent && !topTransparent) || (topTransparent && bottomTransparent))
                        bottomQuads.removeIf(bakedQuad -> bakedQuad.getDirection() == Direction.UP);
                quads.addAll(bottomQuads);
            }
            return quads;
        } else if (MinecraftForgeClient.getRenderLayer() == null) {
            // Rendering the break block animation
        }
        return getFallbackModel().getQuads(state, side, rand, extraData);
    }
}
