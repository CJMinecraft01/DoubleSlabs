package cjminecraft.doubleslabs.neoforge.client.model;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.client.hooks.MixedDoubleSlabBlockClientHooks;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MixedDoubleSlabBakedModel extends NeoForgeDynamicSlabBakedModel {

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                             @NotNull RandomSource rand, @NotNull ModelData data,
                                             @Nullable RenderType renderType) {
        if (!data.has(DYNAMIC_SLAB_STATE_CONTAINER) || renderType == null) {
            return getFallbackModel().getQuads(state, side, rand, data, renderType);
        }

        final @Nullable IDynamicSlabStateContainer stateContainer = data.get(DYNAMIC_SLAB_STATE_CONTAINER);

        if (stateContainer == null) {
            return getFallbackModel().getQuads(state, side, rand, data, renderType);
        }

        final var blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();

        final var shouldNotCull = state == null || state.is(DSNeoForgeBlocks.TRANSPARENT_MIXED_SLAB.get());

        final var quads = new ArrayList<BakedQuad>();

        stateContainer.runOnBlockStates((half, slabState) -> {
            final var model = blockRenderDispatcher.getBlockModel(slabState);

            if (model.getRenderTypes(slabState, rand, data).contains(renderType)) {
                final var modelQuads = model.getQuads(slabState, side, rand, data, renderType);
                final var directionToCull = half == Half.TOP ? Direction.DOWN : Direction.UP;

                quads.addAll(modelQuads.stream().filter(quad -> shouldNotCull || quad.getDirection() != directionToCull)
                        .map(MixedDoubleSlabBlockClientHooks.withCorrectTint(half)).toList());
            }
        });

        return quads;
    }

}
