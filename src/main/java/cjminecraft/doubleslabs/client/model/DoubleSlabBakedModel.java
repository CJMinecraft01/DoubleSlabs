package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.util.SlabCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.ArrayList;
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
    protected List<BakedQuad> getQuads(SlabCache cache) {
        List<BakedQuad> quads = new ArrayList<>();
        if (cache.getPositiveBlockInfo().getBlockState() == null || cache.getNegativeBlockInfo().getBlockState() == null)
            return ClientConstants.getFallbackModel().getQuads(null, cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE);
        if (RenderTypeLookup.canRenderInLayer(cache.getPositiveBlockInfo().getBlockState(), MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null) {
            List<BakedQuad> topQuads = getQuadsForState(cache, true);
//            if (shouldCull)
//                if ((!bottomTransparent && !topTransparent) || (topTransparent && !bottomTransparent) || (topTransparent && bottomTransparent))
//                    topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
            quads.addAll(topQuads);
        }
        if (RenderTypeLookup.canRenderInLayer(cache.getNegativeBlockInfo().getBlockState(), MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null) {
            List<BakedQuad> bottomQuads = getQuadsForState(cache, false);
//            if (shouldCull)
//                if ((!topTransparent && !bottomTransparent) || (bottomTransparent && !topTransparent) || (topTransparent && bottomTransparent))
//                    bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
            quads.addAll(bottomQuads);
        }
        return quads;
    }
}
