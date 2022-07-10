package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class VerticalSlabBakedModel extends DynamicSlabBakedModel {

    public static final VerticalSlabBakedModel INSTANCE = new VerticalSlabBakedModel();

    public static final ModelProperty<Boolean> ROTATE_POSITIVE = new ModelProperty<>();
    public static final ModelProperty<Boolean> ROTATE_NEGATIVE = new ModelProperty<>();
    private final Map<BlockState, BakedModel> models = new HashMap<>();

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, RenderType renderType) {
        if (state != null && extraData.has(POSITIVE_BLOCK) && extraData.has(NEGATIVE_BLOCK)) {
            IBlockInfo positiveBlock = extraData.get(POSITIVE_BLOCK);
            IBlockInfo negativeBlock = extraData.get(NEGATIVE_BLOCK);

            assert positiveBlock != null;
            assert negativeBlock != null;

            BlockState positiveState = positiveBlock.getBlockState();
            BlockState negativeState = negativeBlock.getBlockState();

            if (positiveState == null && negativeState == null)
                return getFallbackModel().getQuads(state, side, rand, extraData, renderType);

            boolean positiveTransparent = positiveState == null || ClientConstants.isTransparent(positiveState);
            boolean negativeTransparent = negativeState == null || ClientConstants.isTransparent(negativeState);
            boolean shouldCull = positiveState != null && negativeState != null && (positiveBlock.getSupport() == null || positiveBlock.getSupport().shouldCull(positiveState, negativeState)) && (negativeBlock.getSupport() == null || negativeBlock.getSupport().shouldCull(negativeState, positiveState)) && DSConfig.CLIENT.shouldCull(positiveState.getBlock()) && DSConfig.CLIENT.shouldCull(negativeState.getBlock()) && (!(positiveTransparent && negativeTransparent) || (positiveState.getBlock() == negativeState.getBlock() && positiveState.is(negativeState.getBlock())));

            boolean renderHalves = extraData.has(RENDER_POSITIVE) && extraData.get(RENDER_POSITIVE) != null;
            boolean renderPositive = renderHalves && extraData.get(RENDER_POSITIVE);

            Direction direction = state.getValue(VerticalSlabBlock.FACING);

            // If the top and bottom states are the same, use the combined block model where possible
            if (positiveState != null && negativeState != null && useDoubleSlabModel(positiveState, negativeState)) {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState);
                if (horizontalSlabSupport != null && horizontalSlabSupport.useDoubleSlabModel(positiveState)) {
                    BlockState doubleState = horizontalSlabSupport.getStateForHalf(positiveBlock.getWorld(), positiveBlock.getPos(), positiveState, SlabType.DOUBLE);
                    BakedModel model = ClientConstants.getVerticalModel(doubleState, direction);
                    if (renderType == null || model.getRenderTypes(doubleState, rand, ModelData.EMPTY).contains(renderType)) {
                        return model.getQuads(doubleState, side, rand, ModelData.EMPTY, renderType);
                    }
                    return Lists.newArrayList();
                }
            }

            List<BakedQuad> quads = Lists.newArrayList();

            if ((!renderHalves || renderPositive) && positiveState != null) {
                BakedModel model = !extraData.has(ROTATE_POSITIVE) || extraData.get(ROTATE_POSITIVE) ? ClientConstants.getVerticalModel(positiveState, direction) : Minecraft.getInstance().getBlockRenderer().getBlockModel(positiveState);
                List<BakedQuad> positiveQuads = getQuadsForState(positiveBlock, model, side, rand, renderType);
                if (shouldCull)
                    if ((!negativeTransparent && !positiveTransparent) || (positiveTransparent && !negativeTransparent) || (positiveTransparent && negativeTransparent))
                        positiveQuads.removeIf(bakedQuad -> bakedQuad.getDirection() == direction.getOpposite());
                quads.addAll(positiveQuads);
            }
            if ((!renderHalves || !renderPositive) && negativeState != null) {
                BakedModel model = !extraData.has(ROTATE_NEGATIVE) || extraData.get(ROTATE_NEGATIVE) ? ClientConstants.getVerticalModel(negativeState, direction) : Minecraft.getInstance().getBlockRenderer().getBlockModel(negativeState);
                List<BakedQuad> negativeQuads = getQuadsForState(negativeBlock, model, side, rand, renderType);
                if (shouldCull)
                    if ((!positiveTransparent && !negativeTransparent) || (negativeTransparent && !positiveTransparent) || (positiveTransparent && negativeTransparent))
                        negativeQuads.removeIf(bakedQuad -> bakedQuad.getDirection() == direction);
                quads.addAll(negativeQuads);
            }
            return quads;
        } else if (renderType == null) {
            // Rendering the break block animation
            BakedModel model = this.models.get(state);
            if (model != null)
                return model.getQuads(state, side, rand, extraData, null);
        }
        return Lists.newArrayList();
//        return getFallbackModel().getQuads(state, side, rand, extraData);
    }

    public void addModel(BakedModel model, BlockState state) {
        this.models.put(state, model);
    }

    public BakedModel getModel(BlockState state) {
        return this.models.get(state);
    }

    private boolean rotateModel(ModelData modelData, ModelProperty<IBlockInfo> property, BlockAndTintGetter world, BlockPos pos) {
        if (modelData.has(property)) {
            IBlockInfo blockInfo = modelData.get(property);
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
    public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
        ModelData modelData = super.getModelData(world, pos, state, tileData);
        return modelData.derive()
                .with(ROTATE_POSITIVE, rotateModel(modelData, POSITIVE_BLOCK, world, pos))
                .with(ROTATE_NEGATIVE, rotateModel(modelData, NEGATIVE_BLOCK, world, pos))
                .build();
    }
}
