package cjminecraft.doubleslabs.fabric.mixin.client.compat.sodium;

import cjminecraft.doubleslabs.fabric.api.client.IDynamicSlabRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockRenderContext.class)
public abstract class BlockRenderContextMixin implements IDynamicSlabRenderContext {

    @Shadow(remap = false)
    private BlockState state;

    @Final
    @Shadow(remap = false)
    private BlockPos.MutableBlockPos pos;

    @Shadow(remap = false)
    private long seed;

    @Shadow private BakedModel model;

    @Override
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, BakedModel model) {
        this.state = blockState;
        this.pos.set(blockPos);

        this.seed = state.getSeed(pos);

        this.model = model;
    }
}
