package cjminecraft.doubleslabs.forge.client.model;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MixedDoubleSlabBakedModel extends ForgeDynamicSlabBakedModel {

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

        final BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();

        List<BakedQuad> quads = new ArrayList<>();

        stateContainer.runOnBlockStates(slabState -> quads.addAll(blockRenderDispatcher.getBlockModel(slabState).getQuads(slabState, side, rand, data, renderType)));

        return quads;
    }

}
