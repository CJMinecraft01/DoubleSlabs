package cjminecraft.doubleslabs.fabric.mixin.client.compat.sodium;

import cjminecraft.doubleslabs.fabric.api.client.IDynamicSlabRenderContext;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlockRenderContext.class)
public abstract class AbstractBlockRenderContextMixin implements IDynamicSlabRenderContext {

    @Shadow(remap = false) protected BlockState state;

    @Shadow(remap = false) protected BlockPos pos;

    @Shadow(remap = false) protected long randomSeed;

    @Shadow(remap = false) protected abstract void prepareAoInfo(boolean modelAo);

    @Override
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAo) {
        this.state = blockState;
        this.pos = blockPos;

        this.randomSeed = state.getSeed(pos);

        this.prepareAoInfo(modelAo);
    }
}
