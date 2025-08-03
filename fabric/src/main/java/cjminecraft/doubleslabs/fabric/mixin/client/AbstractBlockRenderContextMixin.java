package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.fabric.api.client.IDynamicSlabRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("UnstableApiUsage")
@Mixin(AbstractBlockRenderContext.class)
public class AbstractBlockRenderContextMixin implements IDynamicSlabRenderContext {

    @Shadow(remap = false) @Final protected BlockRenderInfo blockInfo;

    @Override
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, BakedModel model) {
        blockInfo.prepareForBlock(blockState, blockPos, model.useAmbientOcclusion());
    }
}
