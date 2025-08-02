package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Shadow
    protected ClientLevel level;

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    public void destroy(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.isAir() || !state.shouldSpawnTerrainParticles()) {
            return;
        }

        if (state.is(DSBlocks.MIXED_SLABS)) {
            if (DynamicSlabBlockClientHooks.addDestroyEffects(level, pos, (ParticleEngine) (Object) this)) {
                ci.cancel();
            }
        }
    }

}
