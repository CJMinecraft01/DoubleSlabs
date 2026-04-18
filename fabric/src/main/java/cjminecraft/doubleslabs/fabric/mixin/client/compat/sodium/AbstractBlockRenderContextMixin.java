package cjminecraft.doubleslabs.fabric.mixin.client.compat.sodium;

import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.fabric.api.client.IDynamicSlabRenderContext;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractBlockRenderContext.class)
public abstract class AbstractBlockRenderContextMixin implements IDynamicSlabRenderContext {

    private final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

    @Shadow(remap = false) protected BlockState state;

    @Shadow(remap = false) protected BlockPos pos;

    @Shadow(remap = false) protected long randomSeed;

    @Shadow(remap = false) protected BlockAndTintGetter level;

    @Shadow(remap = false) protected abstract void prepareAoInfo(boolean modelAo);

    private int cullCompletionFlags;
    private int cullResultFlags;

    @Override
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAo) {
        this.state = blockState;
        this.pos = blockPos;

        this.randomSeed = state.getSeed(pos);

        this.prepareAoInfo(modelAo);

        cullCompletionFlags = 0;
        cullResultFlags = 0;
    }

    @Inject(remap = false, method = "isFaceCulled", at = @At("HEAD"), cancellable = true)
    private void isFaceCulled$doubleslabs(@Nullable Direction face, CallbackInfoReturnable<Boolean> cir) {
        if (face == null) {
            return;
        }

        if (level != null && pos != null) {
            final var actualState = level.getBlockState(pos);

            if (!actualState.is(DSBlocks.VERTICAL_SLAB.get())) {
                return;
            }

            // Use the actual vertical slab state for culling
            final var mask = 1 << face.get3DDataValue();

            if ((cullCompletionFlags & mask) == 0) {
                cullCompletionFlags |= mask;

                if (Block.shouldRenderFace(actualState, level, pos, face, searchPos.setWithOffset(pos, face))) {
                    cullResultFlags |= mask;
                }
            }

            cir.setReturnValue((cullResultFlags & mask) == 0);
        }
    }
}
