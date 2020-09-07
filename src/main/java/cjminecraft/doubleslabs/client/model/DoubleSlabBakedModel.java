package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.CullInfo;
import cjminecraft.doubleslabs.client.util.SlabCacheKey;
import cjminecraft.doubleslabs.client.util.vertex.TintOffsetTransformer;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoubleSlabBakedModel extends DynamicSlabBakedModel {

    private List<BakedQuad> getQuadsForState(SlabCacheKey cache, boolean positive) {
        BlockState state = positive ? cache.getPositiveBlockInfo().getBlockState() : cache.getNegativeBlockInfo().getBlockState();
        if (state == null)
            return new ArrayList<>();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        IModelData tileData = positive ? cache.getPositiveBlockInfo().getTileEntity() != null ? cache.getPositiveBlockInfo().getTileEntity().getModelData() : EmptyModelData.INSTANCE : cache.getNegativeBlockInfo().getTileEntity() != null ? cache.getNegativeBlockInfo().getTileEntity().getModelData() : EmptyModelData.INSTANCE;
        IModelData modelData = model.getModelData(positive ? cache.getPositiveBlockInfo().getWorld() : cache.getNegativeBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), state, tileData);
        return model.getQuads(state, cache.getSide(), cache.getRandom(), modelData).stream().map(quad -> {
            BakedQuadBuilder builder = new BakedQuadBuilder();
            TintOffsetTransformer transformer = new TintOffsetTransformer(builder, positive);
            quad.pipe(transformer);
            return builder.build();
        }).collect(Collectors.toList());
//        return model.getQuads(state, cache.getSide(), cache.getRandom(), modelData).stream().map(quad -> new BakedQuad(quad.getVertexData(), quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getFace(), quad.func_187508_a(), quad.func_239287_f_())).collect(Collectors.toList());
    }

    @Override
    protected Block getBlock() {
        return DSBlocks.DOUBLE_SLAB.get();
    }

    @Override
    protected List<BakedQuad> getQuads(SlabCacheKey cache) {
        List<BakedQuad> quads = new ArrayList<>();
        if (cache.getPositiveBlockInfo().getBlockState() == null || cache.getNegativeBlockInfo().getBlockState() == null)
            return ClientConstants.getFallbackModel().getQuads(null, cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE);
        boolean topTransparent = ClientUtils.isTransparent(cache.getPositiveBlockInfo().getBlockState());
        boolean bottomTransparent = ClientUtils.isTransparent(cache.getNegativeBlockInfo().getBlockState());
        boolean shouldCull = DSConfig.CLIENT.shouldCull(cache.getPositiveBlockInfo().getBlockState().getBlock()) && DSConfig.CLIENT.shouldCull(cache.getNegativeBlockInfo().getBlockState().getBlock()) && (!(topTransparent && bottomTransparent) || (cache.getPositiveBlockInfo().getBlockState().getBlock() == cache.getNegativeBlockInfo().getBlockState().getBlock() && cache.getPositiveBlockInfo().getBlockState().getBlock() == cache.getNegativeBlockInfo().getBlockState().getBlock()));
        // If the top and bottom states are the same, use the combined block model where possible
        if (useDoubleSlabModel(cache.getPositiveBlockInfo().getBlockState(), cache.getNegativeBlockInfo().getBlockState())) {
            IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.isHorizontalSlab(cache.getPositiveBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), cache.getPositiveBlockInfo().getBlockState());
            if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(cache.getPositiveBlockInfo().getBlockState())) {
                BlockState state = horizontalSlabSupport.getStateForHalf(cache.getPositiveBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), cache.getPositiveBlockInfo().getBlockState(), SlabType.DOUBLE);
                if (RenderTypeLookup.canRenderInLayer(state, cache.getRenderLayer()) || cache.getRenderLayer() == null) {
                    IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
                    quads = new ArrayList<>(model.getQuads(state, cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE));
                    if (cache.getSide() != null) {
                        // Only cull the non general sides
                        for (CullInfo cullInfo : cache.getCullInfo()) {
                            if (cullInfo.getPositiveBlockInfo().getBlockState() != null && cullInfo.getNegativeBlockInfo().getBlockState() != null && useDoubleSlabModel(cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getNegativeBlockInfo().getBlockState())) {
                                IHorizontalSlabSupport support = SlabSupport.isHorizontalSlab(cullInfo.getPositiveBlockInfo().getWorld(), cullInfo.getPositiveBlockInfo().getPos(), cullInfo.getPositiveBlockInfo().getBlockState());
                                if (support != null) {
                                    BlockState s = horizontalSlabSupport.getStateForHalf(cullInfo.getPositiveBlockInfo().getWorld(), cullInfo.getPositiveBlockInfo().getPos(), cullInfo.getPositiveBlockInfo().getBlockState(), SlabType.DOUBLE);
                                    if (shouldCull(state, s, cullInfo.getDirection()))
                                        quads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                                }
                            } else if (shouldCull(state, cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getDirection()) || shouldCull(state, cullInfo.getNegativeBlockInfo().getBlockState(), cullInfo.getDirection())) {
                                quads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                            }
                        }
                    }
                    return quads;
                }
                return new ArrayList<>();
            }
        }

        if (RenderTypeLookup.canRenderInLayer(cache.getPositiveBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null) {
            List<BakedQuad> topQuads = getQuadsForState(cache, true);
            if (shouldCull)
                if ((!bottomTransparent && !topTransparent) || (topTransparent && !bottomTransparent) || (topTransparent && bottomTransparent))
                    topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
            if (cache.getSide() != null) {
                for (CullInfo cullInfo : cache.getCullInfo()) {
                    if (shouldCull(cache.getPositiveBlockInfo().getBlockState(), cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getDirection()))
                        topQuads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                }
            }
            quads.addAll(topQuads);
        }
        if (RenderTypeLookup.canRenderInLayer(cache.getNegativeBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null) {
            List<BakedQuad> bottomQuads = getQuadsForState(cache, false);
            if (shouldCull)
                if ((!topTransparent && !bottomTransparent) || (bottomTransparent && !topTransparent) || (topTransparent && bottomTransparent))
                    bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
            if (cache.getSide() != null) {
                for (CullInfo cullInfo : cache.getCullInfo()) {
                    if (shouldCull(cache.getNegativeBlockInfo().getBlockState(), cullInfo.getNegativeBlockInfo().getBlockState(), cullInfo.getDirection()))
                        bottomQuads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                }
            }
            quads.addAll(bottomQuads);
        }
        return quads;
    }
}
