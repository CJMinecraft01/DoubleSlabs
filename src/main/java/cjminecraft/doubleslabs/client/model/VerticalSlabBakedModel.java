package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.CullInfo;
import cjminecraft.doubleslabs.client.util.SlabCacheKey;
import cjminecraft.doubleslabs.client.util.vertex.VerticalSlabTransformer;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.blocks.properties.UnlistedPropertyBoolean;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    protected Block getBlock() {
        return DSBlocks.VERTICAL_SLAB;
    }

    private List<BakedQuad> getQuadsForState(SlabCacheKey cache, boolean positive) {
        IBlockState state = positive ? cache.getPositiveBlockInfo().getBlockState() : cache.getNegativeBlockInfo().getBlockState();
        if (state == null)
            return new ArrayList<>();
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        UnlistedPropertyBoolean property = positive ? VerticalSlabBlock.ROTATE_POSITIVE : VerticalSlabBlock.ROTATE_NEGATIVE;
        boolean rotate = cache.getState() instanceof IExtendedBlockState ? ((IExtendedBlockState) cache.getState()).getValue(property) : true;
        if (!rotate)
            return new ArrayList<>(model.getQuads(state, cache.getSide(), cache.getRandom()));
        EnumFacing direction = cache.getState().getValue(VerticalSlabBlock.FACING);
        EnumFacing side = ClientUtils.rotateFace(cache.getSide(), direction);
        List<BakedQuad> quads = model.getQuads(positive ? cache.getPositiveBlockInfo().getExtendedBlockState() : cache.getNegativeBlockInfo().getExtendedBlockState(), side, cache.getRandom());
        if (DSConfig.CLIENT.useLazyModel(state)) {
            if (quads.size() == 0)
                return new ArrayList<>();
            IBlockState baseState = ((IExtendedBlockState) cache.getState()).getClean();
            if (!positive)
                baseState = baseState.withProperty(VerticalSlabBlock.FACING, direction.getOpposite());
            baseState = baseState.withProperty(VerticalSlabBlock.DOUBLE, false);
            TextureAtlasSprite sprite = quads.get(0).getSprite();
            return this.models.get(baseState).getQuads(baseState, cache.getSide(), cache.getRandom()).stream().map(quad -> new BakedQuad(ClientUtils.changeQuadUVs(quad.getVertexData(), quad.getSprite(), sprite), quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getFace(), sprite, quad.shouldApplyDiffuseLighting(), quad.getFormat())).collect(Collectors.toList());
        }
//        return quads.stream().map(quad -> {
//            BakedQuadBuilder builder = new BakedQuadBuilder();
//            VerticalSlabTransformer transformer = new VerticalSlabTransformer(builder, direction, cache.getSide(), positive);
//            quad.pipe(transformer);
//            return builder.build();
//        }).collect(Collectors.toList());
        if (ClientUtils.areShadersEnabled()) {
            VerticalSlabTransformer transformer = new VerticalSlabTransformer(direction, cache.getSide(), positive);
            return transformer.processMany(quads);
        }
        return quads.stream().map(quad -> {
            int[] vertexData = ClientUtils.rotateVertexData(quad.getVertexData(), direction, cache.getSide());
            return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        }).collect(Collectors.toList());
    }

    @Override
    protected List<BakedQuad> getQuads(SlabCacheKey cache) {
        if (!cache.isValid()) {
            IBakedModel model = this.models.getOrDefault(cache.getState(), null);
            if (model != null)
                return model.getQuads(cache.getState(), cache.getSide(), cache.getRandom());
            return getFallbackModel().getQuads(cache.getState(), cache.getSide(), cache.getRandom());
        }
        List<BakedQuad> quads = new ArrayList<>();
        boolean positiveTransparent = cache.getPositiveBlockInfo().getBlockState() == null || ClientUtils.isTransparent(cache.getPositiveBlockInfo().getBlockState());
        boolean negativeTransparent = cache.getNegativeBlockInfo().getBlockState() == null || ClientUtils.isTransparent(cache.getNegativeBlockInfo().getBlockState());
        boolean shouldCull = cache.getPositiveBlockInfo().getBlockState() != null && cache.getNegativeBlockInfo().getBlockState() != null && DSConfig.CLIENT.shouldCull(cache.getPositiveBlockInfo().getBlockState()) && DSConfig.CLIENT.shouldCull(cache.getNegativeBlockInfo().getBlockState()) && (!(positiveTransparent && negativeTransparent) || (cache.getPositiveBlockInfo().getBlockState().getBlock() == cache.getNegativeBlockInfo().getBlockState().getBlock() && cache.getPositiveBlockInfo().getBlockState().getBlock() == cache.getNegativeBlockInfo().getBlockState().getBlock()));

        EnumFacing direction = cache.getState().getValue(VerticalSlabBlock.FACING);

        // If the top and bottom states are the same, use the combined block model where possible
        if (cache.getPositiveBlockInfo().getBlockState() != null && cache.getNegativeBlockInfo().getBlockState() != null && useDoubleSlabModel(cache.getPositiveBlockInfo().getBlockState(), cache.getNegativeBlockInfo().getBlockState())) {
            IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.isHorizontalSlab(cache.getPositiveBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), cache.getPositiveBlockInfo().getBlockState());
            if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(cache.getPositiveBlockInfo().getBlockState())) {
                IBlockState state = horizontalSlabSupport.getStateForHalf(cache.getPositiveBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), cache.getPositiveBlockInfo().getBlockState(), null);
                if (state.getBlock().canRenderInLayer(state, cache.getRenderLayer()) || cache.getRenderLayer() == null) {
                    IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
                    EnumFacing side = ClientUtils.rotateFace(cache.getSide(), direction);
                    if (DSConfig.CLIENT.useLazyModel(state)) {
                        quads = new ArrayList<>(model.getQuads(state, cache.getSide(), cache.getRandom()));
                    } else {
                        if (!ClientUtils.isOptiFineInstalled()) {
                            quads = model.getQuads(state, side, cache.getRandom()).stream().map(quad -> {
                                int[] vertexData = ClientUtils.rotateVertexData(quad.getVertexData(), direction, cache.getSide());
                                return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
                            }).collect(Collectors.toList());
                        } else {
                            VerticalSlabTransformer transformer = new VerticalSlabTransformer(direction, cache.getSide(), false);
                            quads = transformer.processMany(model.getQuads(state, ClientUtils.rotateFace(cache.getSide(), direction), cache.getRandom()));
                        }
                    }
                    if (cache.getSide() != null) {
                        // Only cull the non general sides
                        for (CullInfo cullInfo : cache.getCullInfo()) {
                            if (cullInfo.getOtherState().getValue(VerticalSlabBlock.DOUBLE) && cullInfo.getPositiveBlockInfo().getBlockState() != null && cullInfo.getNegativeBlockInfo().getBlockState() != null && useDoubleSlabModel(cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getNegativeBlockInfo().getBlockState())) {
                                IHorizontalSlabSupport support = SlabSupport.isHorizontalSlab(cullInfo.getPositiveBlockInfo().getWorld(), cullInfo.getPositiveBlockInfo().getPos(), cullInfo.getPositiveBlockInfo().getBlockState());
                                // try with vertical slabs
                                if (support != null) {
                                    IBlockState s = support.getStateForHalf(cullInfo.getPositiveBlockInfo().getWorld(), cullInfo.getPositiveBlockInfo().getPos(), cullInfo.getPositiveBlockInfo().getBlockState(), null);
                                    if (shouldCull(state, s, cullInfo.getDirection()))
                                        quads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                                }
                            } else if (cullInfo.getOtherState().getValue(VerticalSlabBlock.FACING) == cullInfo.getDirection() && shouldCull(state, cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getDirection()) || shouldCull(state, cullInfo.getNegativeBlockInfo().getBlockState(), cullInfo.getDirection())) {
                                quads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                            }
                        }
                    }
                    return quads;
                }
                return new ArrayList<>();
            } else {
                // try with vertical slabs
            }
        }

        if (cache.getPositiveBlockInfo().getBlockState() != null && (cache.getPositiveBlockInfo().getBlockState().getBlock().canRenderInLayer(cache.getPositiveBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null)) {
            List<BakedQuad> positiveQuads = getQuadsForState(cache, true);
            if (shouldCull)
                if ((!negativeTransparent && !positiveTransparent) || (positiveTransparent && !negativeTransparent) || (positiveTransparent && negativeTransparent))
                    positiveQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction.getOpposite());
                // might not be able to use the following cull technique as I need to handle the various faces of a vertical slab
            if (cache.getSide() != null) {
                for (CullInfo cullInfo : cache.getCullInfo()) {
                    EnumFacing otherDirection = cullInfo.getOtherState().getValue(VerticalSlabBlock.FACING);
                    if (!cullInfo.getState().getValue(VerticalSlabBlock.DOUBLE) && otherDirection.getAxis() != direction.getAxis())
                        continue;
                    boolean positive = (direction == otherDirection && cullInfo.getDirection().getAxis() != direction.getAxis()) || otherDirection.getAxis() != direction.getAxis() || (direction == otherDirection.getOpposite() && cullInfo.getDirection().getAxis() == direction.getAxis());
                    if (shouldCull(cache.getPositiveBlockInfo().getBlockState(), positive ? cullInfo.getPositiveBlockInfo().getBlockState() : cullInfo.getNegativeBlockInfo().getBlockState(), cullInfo.getDirection()))
                        positiveQuads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                }
            }
            quads.addAll(positiveQuads);
        }
        if (cache.getNegativeBlockInfo().getBlockState() != null && (cache.getNegativeBlockInfo().getBlockState().getBlock().canRenderInLayer(cache.getNegativeBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null)) {
            List<BakedQuad> negativeQuads = getQuadsForState(cache, false);
            if (shouldCull)
                if ((!positiveTransparent && !negativeTransparent) || (negativeTransparent && !positiveTransparent) || (positiveTransparent && negativeTransparent))
                    negativeQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction);
            if (cache.getSide() != null) {
                for (CullInfo cullInfo : cache.getCullInfo()) {
                    EnumFacing otherDirection = cullInfo.getOtherState().getValue(VerticalSlabBlock.FACING);
                    if (!cullInfo.getState().getValue(VerticalSlabBlock.DOUBLE) && otherDirection.getAxis() != direction.getAxis())
                        continue;
                    boolean negative = (direction == otherDirection && cullInfo.getDirection().getAxis() != direction.getAxis()) || otherDirection.getAxis() != direction.getAxis() || (direction == otherDirection.getOpposite() && cullInfo.getDirection().getAxis() == direction.getAxis());
                    if (shouldCull(cache.getNegativeBlockInfo().getBlockState(), negative ? cullInfo.getNegativeBlockInfo().getBlockState() : cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getDirection()))
                        negativeQuads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                }
            }
            quads.addAll(negativeQuads);
        }
        return quads;
    }
}
