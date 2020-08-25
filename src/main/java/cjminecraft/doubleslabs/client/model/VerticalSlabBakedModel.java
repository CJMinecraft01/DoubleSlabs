package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.SlabCache;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class VerticalSlabBakedModel extends DynamicSlabBakedModel {

    public static final ModelProperty<Boolean> ROTATE_POSITIVE = new ModelProperty<>();
    public static final ModelProperty<Boolean> ROTATE_NEGATIVE = new ModelProperty<>();

    private final Map<BlockState, IBakedModel> models = new HashMap<>();

    public void addModel(IBakedModel model, BlockState state) {
        this.models.put(state, model);
    }

    @Override
    protected Block getBlock() {
        return DSBlocks.VERTICAL_SLAB.get();
    }

    private List<BakedQuad> getQuadsForState(SlabCache cache, boolean positive) {
        BlockState state = positive ? cache.getPositiveBlockInfo().getBlockState() : cache.getNegativeBlockInfo().getBlockState();
        if (state == null)
            return ImmutableList.of();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        IModelData tileData = positive ? cache.getPositiveBlockInfo().getTileEntity() != null ? cache.getPositiveBlockInfo().getTileEntity().getModelData() : EmptyModelData.INSTANCE : cache.getNegativeBlockInfo().getTileEntity() != null ? cache.getNegativeBlockInfo().getTileEntity().getModelData() : EmptyModelData.INSTANCE;
        IModelData modelData = model.getModelData(positive ? cache.getPositiveBlockInfo().getWorld() : cache.getNegativeBlockInfo().getWorld(), cache.getPositiveBlockInfo().getPos(), state, tileData);
        ModelProperty<Boolean> property = positive ? ROTATE_POSITIVE : ROTATE_NEGATIVE;
        boolean rotate = cache.getModelData().hasProperty(property) && cache.getModelData().getData(property) != null ? cache.getModelData().getData(property) : true;
        if (!rotate)
            return new ArrayList<>(model.getQuads(state, cache.getSide(), cache.getRandom(), modelData));
        Direction direction = cache.getOriginalState().get(VerticalSlabBlock.FACING);
        Direction side = ClientUtils.rotateFace(cache.getSide(), direction);
        List<BakedQuad> quads = model.getQuads(state, side, cache.getRandom(), modelData);
        if (DSConfig.CLIENT.useLazyModel(state.getBlock())) {
            if (quads.size() == 0)
                return ImmutableList.of();
            BlockState baseState = positive ? cache.getOriginalState() : cache.getOriginalState().with(VerticalSlabBlock.FACING, direction.getOpposite()).with(VerticalSlabBlock.DOUBLE, false);
            TextureAtlasSprite sprite = quads.get(0).func_187508_a();
            return this.models.get(baseState).getQuads(baseState, cache.getSide(), cache.getRandom(), EmptyModelData.INSTANCE).stream().map(quad -> new BakedQuad(ClientUtils.changeQuadUVs(quad.getVertexData(), quad.func_187508_a(), sprite), quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, quad.getFace(), sprite, quad.func_239287_f_())).collect(Collectors.toList());
        }
        return quads.stream().map(quad -> {
            int[] vertexData = ClientUtils.rotateVertexData(quad.getVertexData(), direction, cache.getSide());
            return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() + (positive ? ClientConstants.TINT_OFFSET : 0) : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.func_187508_a(), quad.func_239287_f_());
        }).collect(Collectors.toList());
    }

    @Override
    protected List<BakedQuad> getQuads(SlabCache cache) {
        List<BakedQuad> quads = new ArrayList<>();
        if (cache.getPositiveBlockInfo().getBlockState() != null && (RenderTypeLookup.canRenderInLayer(cache.getPositiveBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null)) {
            List<BakedQuad> positiveQuads = getQuadsForState(cache, true);
            quads.addAll(positiveQuads);
        }
        if (cache.getNegativeBlockInfo().getBlockState() != null && (RenderTypeLookup.canRenderInLayer(cache.getNegativeBlockInfo().getBlockState(), cache.getRenderLayer()) || cache.getRenderLayer() == null)) {
            List<BakedQuad> negativeQuads = getQuadsForState(cache, false);
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
