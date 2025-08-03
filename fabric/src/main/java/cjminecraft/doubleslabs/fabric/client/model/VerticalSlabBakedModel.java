package cjminecraft.doubleslabs.fabric.client.model;

import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.fabric.api.client.IDynamicSlabRenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class VerticalSlabBakedModel extends FabricDynamicSlabBakedModel {

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                               Supplier<RandomSource> randomSupplier, RenderContext context) {
        final var type = state.getValue(VerticalSlabBlock.TYPE);
        final var axis = state.getValue(VerticalSlabBlock.AXIS);

        final var blockEntity = blockView.getBlockEntity(pos);

        if (blockEntity instanceof DynamicSlabBlockEntity<?> dynamicSlabBlockEntity) {
            final var blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();
            final var slabHelper = Internal.getSlabHelper();

            dynamicSlabBlockEntity.runOnBlockStates((half, slabState) -> {
                // Only emit quads for the present vertical slab half
                if (type != VerticalSlabType.DOUBLE && type.getHalf() != half) {
                    return;
                }

                final var direction = type.getDirection(half, axis);
                final var model = slabHelper.isVerticalSlab(slabState) ?
                        blockRenderDispatcher.getBlockModel(slabState) :
                        ClientInternal.getVerticalSlabModelHelper().getVerticalSlabModel(slabState, direction);

                ((IDynamicSlabRenderContext) context).prepareForBlock(slabState, pos, model);

                model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            });
        }
    }
}
