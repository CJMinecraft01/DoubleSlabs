package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.CullInfo;
import cjminecraft.doubleslabs.client.util.SlabCacheKey;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class VerticalSlabBakedModel extends DynamicSlabBakedModel {

    public static final VerticalSlabBakedModel INSTANCE = new VerticalSlabBakedModel();

    public static final ModelProperty<Boolean> ROTATE_POSITIVE = new ModelProperty<>();
    public static final ModelProperty<Boolean> ROTATE_NEGATIVE = new ModelProperty<>();

    private final Map<BlockState, IBakedModel> models = new HashMap<>();

    public void addModel(IBakedModel model, BlockState state) {
        this.models.put(state, model);
    }

    public IBakedModel getModel(BlockState state) {
        return this.models.get(state);
    }

    @Override
    protected Block getBlock() {
        return DSBlocks.VERTICAL_SLAB.get();
    }

    private List<BakedQuad> getQuadsForState(SlabCacheKey cache, boolean positive) {
        BlockState state = positive ? cache.getPositiveBlockInfo().getBlockState() : cache.getNegativeBlockInfo().getBlockState();
        if (state == null)
            return new ArrayList<>();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        IModelData tileData = positive ? cache.getPositiveBlockInfo().getTileEntity() != null ? cache.getPositiveBlockInfo().getTileEntity().getModelData() : EmptyModelData.INSTANCE : cache.getNegativeBlockInfo().getTileEntity() != null ? cache.getNegativeBlockInfo().getTileEntity().getModelData() : EmptyModelData.INSTANCE;
        IModelData modelData = model.getModelData(positive ? cache.getPositiveBlockInfo().getWorld() : cache.getNegativeBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), state, tileData);
        ModelProperty<Boolean> property = positive ? ROTATE_POSITIVE : ROTATE_NEGATIVE;
        boolean rotate = cache.getModelData().hasProperty(property) && cache.getModelData().getData(property) != null ? cache.getModelData().getData(property) : true;
        if (!rotate)
            return new ArrayList<>(model.getQuads(state, cache.getSide(), cache.getRandom(), modelData));
        Direction direction = cache.getState().get(VerticalSlabBlock.FACING);
        Direction side = ClientUtils.rotateFace(cache.getSide(), direction);
        List<BakedQuad> quads = model.getQuads(state, side, cache.getRandom(), modelData);
        if (DSConfig.CLIENT.useLazyModel(state.getBlock())) {
            if (quads.size() == 0)
                return new ArrayList<>();
            BlockState baseState = positive ? cache.getState() : cache.getState().with(VerticalSlabBlock.FACING, direction.getOpposite()).with(VerticalSlabBlock.DOUBLE, false);
            TextureAtlasSprite sprite = quads.get(0).func_187508_a();
            return this.models.get(baseState).getQuads(baseState, cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE).stream().map(quad -> new BakedQuad(ClientUtils.changeQuadUVs(quad.getVertexData(), quad.func_187508_a(), sprite), quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getFace(), sprite, quad.func_239287_f_())).collect(Collectors.toList());
        }
        return quads.stream().map(quad -> {
            int[] vertexData = ClientUtils.rotateVertexData(quad.getVertexData(), direction, cache.getSide());
            return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.func_187508_a(), quad.func_239287_f_());
        }).collect(Collectors.toList());
    }

    @Override
    protected List<BakedQuad> getQuads(SlabCacheKey cache) {
        if (!cache.isValid()) {
            IBakedModel model = this.models.getOrDefault(cache.getState(), null);
            if (model != null)
                return model.getQuads(cache.getState(), cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE);
            return getFallbackModel().getQuads(cache.getState(), cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE);
        }
        List<BakedQuad> quads = new ArrayList<>();
        boolean positiveTransparent = cache.getPositiveBlockInfo().getBlockState() == null || ClientUtils.isTransparent(cache.getPositiveBlockInfo().getBlockState());
        boolean negativeTransparent = cache.getNegativeBlockInfo().getBlockState() == null || ClientUtils.isTransparent(cache.getNegativeBlockInfo().getBlockState());
        boolean shouldCull = cache.getPositiveBlockInfo().getBlockState() != null && cache.getNegativeBlockInfo().getBlockState() != null && DSConfig.CLIENT.shouldCull(cache.getPositiveBlockInfo().getBlockState().getBlock()) && DSConfig.CLIENT.shouldCull(cache.getNegativeBlockInfo().getBlockState().getBlock()) && (!(positiveTransparent && negativeTransparent) || (cache.getPositiveBlockInfo().getBlockState().getBlock() == cache.getNegativeBlockInfo().getBlockState().getBlock() && cache.getPositiveBlockInfo().getBlockState().isIn(cache.getNegativeBlockInfo().getBlockState().getBlock())));

        Direction direction = cache.getState().get(VerticalSlabBlock.FACING);

        // TODO improve culling

        // If the top and bottom states are the same, use the combined block model where possible
        if (cache.getPositiveBlockInfo().getBlockState() != null && cache.getNegativeBlockInfo().getBlockState() != null && useDoubleSlabModel(cache.getPositiveBlockInfo().getBlockState(), cache.getNegativeBlockInfo().getBlockState())) {
            IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.isHorizontalSlab(cache.getPositiveBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), cache.getPositiveBlockInfo().getBlockState());
            if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(cache.getPositiveBlockInfo().getBlockState())) {
                BlockState state = horizontalSlabSupport.getStateForHalf(cache.getPositiveBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), cache.getPositiveBlockInfo().getBlockState(), SlabType.DOUBLE);
                if (RenderTypeLookup.canRenderInLayer(state, cache.getRenderLayer()) || cache.getRenderLayer() == null) {
                    IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
                    Direction side = ClientUtils.rotateFace(cache.getSide(), direction);
                    if (DSConfig.CLIENT.useLazyModel(state.getBlock())) {
                        quads = new ArrayList<>(model.getQuads(state, cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE));
                    } else {
                        quads = model.getQuads(state, side, cache.getRandom(), EmptyModelData.INSTANCE).stream().map(quad -> {
                            int[] vertexData = ClientUtils.rotateVertexData(quad.getVertexData(), direction, cache.getSide());
                            return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.func_187508_a(), quad.func_239287_f_());
                        }).collect(Collectors.toList());
                    }
                    if (cache.getSide() != null) {
                        // Only cull the non general sides
                        for (CullInfo cullInfo : cache.getCullInfo()) {
                            if (cullInfo.getOtherState().get(VerticalSlabBlock.DOUBLE) && cullInfo.getPositiveBlockInfo().getBlockState() != null && cullInfo.getNegativeBlockInfo().getBlockState() != null && useDoubleSlabModel(cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getNegativeBlockInfo().getBlockState())) {
                                IHorizontalSlabSupport support = SlabSupport.isHorizontalSlab(cullInfo.getPositiveBlockInfo().getWorld(), cullInfo.getPositiveBlockInfo().getPos(), cullInfo.getPositiveBlockInfo().getBlockState());
                                // try with vertical slabs
                                if (support != null) {
                                    BlockState s = horizontalSlabSupport.getStateForHalf(cullInfo.getPositiveBlockInfo().getWorld(), cullInfo.getPositiveBlockInfo().getPos(), cullInfo.getPositiveBlockInfo().getBlockState(), SlabType.DOUBLE);
                                    if (shouldCull(state, s, cullInfo.getDirection()))
                                        quads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                                }
                            } else if (cullInfo.getOtherState().get(VerticalSlabBlock.FACING) == cullInfo.getDirection() && shouldCull(state, cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getDirection()) || shouldCull(state, cullInfo.getNegativeBlockInfo().getBlockState(), cullInfo.getDirection())) {
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

        if (cache.getPositiveBlockInfo().getBlockState() != null && (RenderTypeLookup.canRenderInLayer(cache.getPositiveBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null)) {
            List<BakedQuad> positiveQuads = getQuadsForState(cache, true);
            if (shouldCull)
                if ((!negativeTransparent && !positiveTransparent) || (positiveTransparent && !negativeTransparent) || (positiveTransparent && negativeTransparent))
                    positiveQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction.getOpposite());
                // might not be able to use the following cull technique as I need to handle the various faces of a vertical slab
            if (cache.getSide() != null) {
                for (CullInfo cullInfo : cache.getCullInfo()) {
                    Direction otherDirection = cullInfo.getOtherState().get(VerticalSlabBlock.FACING);
                    if (!cullInfo.getState().get(VerticalSlabBlock.DOUBLE) && otherDirection.getAxis() != direction.getAxis())
                        continue;
                    boolean positive = otherDirection.getAxis() != direction.getAxis() || (direction == otherDirection.getOpposite() && cullInfo.getDirection().getAxis() == direction.getAxis());
                    if (shouldCull(cache.getPositiveBlockInfo().getBlockState(), positive ? cullInfo.getPositiveBlockInfo().getBlockState() : cullInfo.getNegativeBlockInfo().getBlockState(), cullInfo.getDirection()))
                        positiveQuads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                }
            }
            quads.addAll(positiveQuads);
        }
        if (cache.getNegativeBlockInfo().getBlockState() != null && (RenderTypeLookup.canRenderInLayer(cache.getNegativeBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null)) {
            List<BakedQuad> negativeQuads = getQuadsForState(cache, false);
            if (shouldCull)
                if ((!positiveTransparent && !negativeTransparent) || (negativeTransparent && !positiveTransparent) || (positiveTransparent && negativeTransparent))
                    negativeQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction);
            if (cache.getSide() != null) {
                for (CullInfo cullInfo : cache.getCullInfo()) {
                    Direction otherDirection = cullInfo.getOtherState().get(VerticalSlabBlock.FACING);
                    if (!cullInfo.getState().get(VerticalSlabBlock.DOUBLE) && otherDirection.getAxis() != direction.getAxis())
                        continue;
                    boolean negative = otherDirection.getAxis() != direction.getAxis() || (direction == otherDirection.getOpposite() && cullInfo.getDirection().getAxis() == direction.getAxis());
                    if (shouldCull(cache.getNegativeBlockInfo().getBlockState(), negative ? cullInfo.getNegativeBlockInfo().getBlockState() : cullInfo.getPositiveBlockInfo().getBlockState(), cullInfo.getDirection()))
                        negativeQuads.removeIf(quad -> quad.getFace() == cullInfo.getDirection());
                }
            }
            quads.addAll(negativeQuads);
        }
        return quads;
    }

    private boolean rotateModel(IModelData modelData, ModelProperty<IBlockInfo> property, IBlockDisplayReader world, BlockPos pos) {
        if (modelData.hasProperty(property)) {
            IBlockInfo blockInfo = modelData.getData(property);
            if (blockInfo != null && blockInfo.getBlockState() != null) {
                IVerticalSlabSupport support = SlabSupport.getVerticalSlabSupport(world, pos, blockInfo.getBlockState());
                if (support != null)
                    return support.rotateModel(blockInfo.getWorld(), pos, blockInfo.getBlockState());
            }
        }
        return true;
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        IModelData modelData = super.getModelData(world, pos, state, tileData);
        modelData.setData(ROTATE_POSITIVE, rotateModel(modelData, POSITIVE_BLOCK, world, pos));
        modelData.setData(ROTATE_NEGATIVE, rotateModel(modelData, NEGATIVE_BLOCK, world, pos));
        return modelData;
    }
}
