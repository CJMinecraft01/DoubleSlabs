package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class DoubleSlabBakedModel extends DynamicSlabBakedModel {

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType renderType) {
        if (extraData.has(POSITIVE_BLOCK) && extraData.has(NEGATIVE_BLOCK)) {
            IBlockInfo positiveBlock = extraData.get(POSITIVE_BLOCK);
            IBlockInfo negativeBlock = extraData.get(NEGATIVE_BLOCK);

            assert positiveBlock != null;
            assert negativeBlock != null;

            BlockState positiveState = positiveBlock.getBlockState();
            BlockState negativeState = negativeBlock.getBlockState();

            if (positiveState == null || negativeState == null)
                return getFallbackModel().getQuads(state, side, rand, extraData, renderType);

            boolean renderHalves = extraData.has(RENDER_POSITIVE) && extraData.get(RENDER_POSITIVE) != null;
            boolean renderPositive = renderHalves && extraData.get(RENDER_POSITIVE);

            boolean topTransparent = ClientConstants.isTransparent(positiveState);
            boolean bottomTransparent = ClientConstants.isTransparent(negativeState);
            boolean shouldCull = (positiveBlock.getSupport() == null || positiveBlock.getSupport().shouldCull(positiveState, negativeState)) && (negativeBlock.getSupport() == null || negativeBlock.getSupport().shouldCull(negativeState, positiveState)) && DSConfig.CLIENT.shouldCull(positiveState.getBlock()) && DSConfig.CLIENT.shouldCull(negativeState.getBlock()) && (!(topTransparent && bottomTransparent) || (positiveState.getBlock() == negativeState.getBlock() && positiveState.is(negativeState.getBlock())));

            // If the top and bottom states are the same, use the combined block model where possible
            if (useDoubleSlabModel(positiveState, negativeState)) {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState);
                if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(positiveState)) {
                    BlockState doubleState = horizontalSlabSupport.getStateForHalf(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState, SlabType.DOUBLE);
                    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(doubleState);
                    if (renderType == null || model.getRenderTypes(doubleState, rand, ModelData.EMPTY).contains(renderType)) {
                        return model.getQuads(doubleState, side, rand, ModelData.EMPTY, renderType);
                    }
                    return Lists.newArrayList();
                }
            }

            List<BakedQuad> quads = Lists.newArrayList();

            if (!renderHalves || renderPositive) {
                List<BakedQuad> topQuads = getQuadsForState(positiveBlock, side, rand, renderType);
                if (shouldCull)
                    if ((!bottomTransparent && !topTransparent) || (topTransparent && !bottomTransparent) || (topTransparent && bottomTransparent))
                        topQuads.removeIf(bakedQuad -> bakedQuad.getDirection() == Direction.DOWN);
                quads.addAll(topQuads);
            }
            if (!renderHalves || !renderPositive) {
                List<BakedQuad> bottomQuads = getQuadsForState(negativeBlock, side, rand, renderType);
                if (shouldCull)
                    if ((!topTransparent && !bottomTransparent) || (bottomTransparent && !topTransparent) || (topTransparent && bottomTransparent))
                        bottomQuads.removeIf(bakedQuad -> bakedQuad.getDirection() == Direction.UP);
                quads.addAll(bottomQuads);
            }
            return quads;
        } else if (renderType == null) {
            // Rendering the break block animation
        }
        return getFallbackModel().getQuads(state, side, rand, extraData, renderType);
    }
}
