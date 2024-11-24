package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Redirect(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private <T extends ParticleOptions> int checkFallDamage$sendParticles$doubleSlabs(ServerLevel instance, T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed, @Local(ordinal = 0, argsOnly = true) BlockPos pos) {
        ParticleOptions particle = type;
        if (type instanceof BlockParticleOption blockParticleOption) {
            if (blockParticleOption.getState().is(DSBlocks.MIXED_SLAB.get())) {
                particle = MixedDoubleSlabBlockHooks.getParticleForTopSlab(instance, pos).orElse(blockParticleOption);
            }
        }

        return instance.sendParticles(particle, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
    }

}
