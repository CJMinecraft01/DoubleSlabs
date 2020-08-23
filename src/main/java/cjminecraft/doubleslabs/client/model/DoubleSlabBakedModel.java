package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.CullInfo;
import cjminecraft.doubleslabs.client.util.SlabCache;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.old.Utils;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DoubleSlabBakedModel extends DynamicSlabBakedModel {

    private List<BakedQuad> getQuadsForState(SlabCache cache, boolean positive) {
        BlockState state = positive ? cache.getPositiveBlockInfo().getBlockState() : cache.getNegativeBlockInfo().getBlockState();
        if (state == null)
            return ImmutableList.of();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        return model.getQuads(state, cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE).stream().map(quad -> new BakedQuad(quad.getVertexData(), quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getFace(), quad.func_187508_a(), quad.func_239287_f_())).collect(Collectors.toList());
    }

    @Override
    protected Block getBlock() {
        return DSBlocks.DOUBLE_SLAB.get();
    }

    private boolean shouldCull(BlockState state, BlockState neighbour, Direction direction) {
        return state.isSideInvisible(neighbour, direction);
    }

    @Override
    protected List<BakedQuad> getQuads(SlabCache cache) {
        List<BakedQuad> quads = new ArrayList<>();
        if (cache.getPositiveBlockInfo().getBlockState() == null || cache.getNegativeBlockInfo().getBlockState() == null)
            return ClientConstants.getFallbackModel().getQuads(null, cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE);
        boolean shouldCull = DSConfig.CLIENT.shouldCull(cache.getPositiveBlockInfo().getBlockState().getBlock()) && DSConfig.CLIENT.shouldCull(cache.getNegativeBlockInfo().getBlockState().getBlock());
        boolean topTransparent = ClientUtils.isTransparent(cache.getPositiveBlockInfo().getBlockState());
        boolean bottomTransparent = ClientUtils.isTransparent(cache.getNegativeBlockInfo().getBlockState());
        if (RenderTypeLookup.canRenderInLayer(cache.getPositiveBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null) {
            List<BakedQuad> topQuads = getQuadsForState(cache, true);
            if (shouldCull)
                if ((!bottomTransparent && !topTransparent) || (topTransparent && !bottomTransparent) || (topTransparent && bottomTransparent))
                    topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
            for (CullInfo cullInfo : cache.getCullInfo()) {
                if (shouldCull(cache.getPositiveBlockInfo().getBlockState(), cullInfo.getPositiveBlock().getBlockState(), cullInfo.getDirection()))
                    topQuads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
            }
            quads.addAll(topQuads);
        }
        if (RenderTypeLookup.canRenderInLayer(cache.getNegativeBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null) {
            List<BakedQuad> bottomQuads = getQuadsForState(cache, false);
            if (shouldCull)
                if ((!topTransparent && !bottomTransparent) || (bottomTransparent && !topTransparent) || (topTransparent && bottomTransparent))
                    bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
            for (CullInfo cullInfo : cache.getCullInfo()) {
                if (shouldCull(cache.getNegativeBlockInfo().getBlockState(), cullInfo.getNegativeBlock().getBlockState(), cullInfo.getDirection()))
                    bottomQuads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
            }
            quads.addAll(bottomQuads);
        }
        return quads;
    }

    @Override
    protected List<Direction> getCullDirections() {
        return Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    }
}
