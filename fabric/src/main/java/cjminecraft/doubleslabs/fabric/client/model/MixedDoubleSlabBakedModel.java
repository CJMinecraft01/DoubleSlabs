package cjminecraft.doubleslabs.fabric.client.model;

import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.fabric.mixin.client.AbstractBlockRenderContextAccessor;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class MixedDoubleSlabBakedModel extends FabricDynamicSlabBakedModel {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                               Supplier<RandomSource> randomSupplier, RenderContext context) {
        final var blockEntity = blockView.getBlockEntity(pos);

        if (blockEntity instanceof DynamicSlabBlockEntity<?> dynamicSlabBlockEntity) {
            final var blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();

            dynamicSlabBlockEntity.runOnBlockStates(slabState -> {
                final var model = blockRenderDispatcher.getBlockModel(slabState);

                ((AbstractBlockRenderContextAccessor) context).getBlockInfo().prepareForBlock(slabState, pos, model.useAmbientOcclusion());

                model.emitBlockQuads(blockView, slabState, pos, randomSupplier, context);
            });
        }
    }
}
