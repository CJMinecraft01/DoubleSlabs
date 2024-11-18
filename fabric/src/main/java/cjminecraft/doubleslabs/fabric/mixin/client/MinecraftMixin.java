package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.client.hooks.MixedDoubleSlabBlockClientHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Nullable public HitResult hitResult;
    @Shadow @Nullable public ClientLevel level;

    @Redirect(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;crack(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)V"))
    private void continueAttack$crack$doubleslabs(ParticleEngine instance, BlockPos pos, Direction direction) {
        assert level != null;

        BlockState state = level.getBlockState(pos);

        if (state.is(DSBlocks.MIXED_SLAB.get())) {
            assert hitResult != null;

            MixedDoubleSlabBlockClientHooks.addHitEffects(level, pos, hitResult, direction, instance);
        } else {
            instance.crack(pos, direction);
        }
    }

}
