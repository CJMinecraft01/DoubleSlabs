package cjminecraft.doubleslabs.forge.client.model;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VerticalSlabBakedModel extends ForgeDynamicSlabBakedModel {

    private final BakedModel baseModel;

    public VerticalSlabBakedModel(BakedModel baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
                                    ModelData data, @Nullable RenderType renderType) {
        if (!data.has(DYNAMIC_SLAB_STATE_CONTAINER) || renderType == null || state == null) {
            return baseModel.getQuads(state, side, rand, data, renderType);
        }

        final @Nullable IDynamicSlabStateContainer stateContainer = data.get(DYNAMIC_SLAB_STATE_CONTAINER);

        if (stateContainer == null) {
            return baseModel.getQuads(state, side, rand, data, renderType);
        }

        final var type = state.getValue(VerticalSlabBlock.TYPE);
        final var axis = state.getValue(VerticalSlabBlock.AXIS);

        final var blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
        final var slabHelper = Internal.getSlabHelper();

        final var quads = new ArrayList<BakedQuad>();

        stateContainer.runOnBlockStates((half, slabState) -> {
            // Only add quads for the present vertical slab half
            if (type != VerticalSlabType.DOUBLE && type.getHalf() != half) {
                return;
            }

            final var direction = type.getDirection(half, axis);
            final var model = slabHelper.isVerticalSlab(slabState) ?
                    blockRenderDispatcher.getBlockModel(slabState) :
                    ClientInternal.getVerticalSlabModelHelper().getVerticalSlabModel(slabState, direction);

            if (model.getRenderTypes(slabState, rand, data).contains(renderType)) {
                final var modelQuads = model.getQuads(slabState, side, rand, data, renderType);

                quads.addAll(modelQuads.stream().map(DynamicSlabBlockClientHooks.withCorrectTint(half)).toList());
            }
        });

        return quads;
    }
}
