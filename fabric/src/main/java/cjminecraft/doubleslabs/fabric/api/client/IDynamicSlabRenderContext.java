package cjminecraft.doubleslabs.fabric.api.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IDynamicSlabRenderContext {

    default void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAo) {

    }

}
