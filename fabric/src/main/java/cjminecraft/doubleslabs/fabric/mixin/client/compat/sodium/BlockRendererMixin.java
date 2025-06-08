package cjminecraft.doubleslabs.fabric.mixin.client.compat.sodium;

import cjminecraft.doubleslabs.fabric.api.client.IDynamicSlabRenderContext;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin extends AbstractBlockRenderContext implements IDynamicSlabRenderContext {

    @Shadow(remap = false) private @Nullable ColorProvider<BlockState> colorProvider;

    @Shadow(remap = false) @Final private ColorProviderRegistry colorProviderRegistry;

    @Override
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAo) {
        this.state = blockState;
        this.pos = blockPos;

        this.randomSeed = state.getSeed(pos);

        this.colorProvider = this.colorProviderRegistry.getColorProvider(state.getBlock());

        this.type = ItemBlockRenderTypes.getChunkRenderType(state);

        this.prepareCulling(true);
        this.prepareAoInfo(modelAo);
    }
}
