package cjminecraft.doubleslabs.neoforge.client.model;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.client.hooks.MixedDoubleSlabBlockClientHooks;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static cjminecraft.doubleslabs.client.ClientInternal.getFallbackModel;

public class VerticalSlabBakedModel extends NeoForgeDynamicSlabBakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        if (!data.has(DYNAMIC_SLAB_STATE_CONTAINER) || renderType == null || state == null) {
            return getFallbackModel().getQuads(state, side, rand, data, renderType);
        }

        final @Nullable IDynamicSlabStateContainer stateContainer = data.get(DYNAMIC_SLAB_STATE_CONTAINER);

        if (stateContainer == null) {
            return getFallbackModel().getQuads(state, side, rand, data, renderType);
        }

        final var type = state.getValue(VerticalSlabBlock.TYPE);
        final var axis = state.getValue(VerticalSlabBlock.AXIS);

        final var quads = new ArrayList<BakedQuad>();

        stateContainer.runOnBlockStates((half, slabState) -> {
            final var direction = type.getDirection(half, axis);
            final var model = ClientInternal.getVerticalSlabModelHelper().getVerticalSlabModel(slabState, direction);

            if (model.getRenderTypes(slabState, rand, data).contains(renderType)) {
                final var modelQuads = model.getQuads(slabState, side, rand, data, renderType);

                quads.addAll(modelQuads.stream().map(MixedDoubleSlabBlockClientHooks.withCorrectTint(half)).toList());
            }
        });

        return quads;
    }
}
