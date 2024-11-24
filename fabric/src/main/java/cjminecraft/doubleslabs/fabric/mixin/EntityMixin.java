package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow private Level level;

    @Redirect(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"))
    private SoundType playStepSound$getSoundType$doubleslabs(BlockState instance, BlockPos pos) {
        if (instance.is(DSBlocks.MIXED_SLAB.get())) {
            return MixedDoubleSlabBlockHooks.getSoundType(level, pos, (Entity) (Object) this).orElseGet(instance::getSoundType);
        }
        return instance.getSoundType();
    }

    @Redirect(method = "spawnSprintParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void spawnSprintParticle$addParticle$doubleSlabs(Level instance, ParticleOptions type, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Local(ordinal = 0) BlockPos pos) {
        if (type instanceof BlockParticleOption blockParticleOption) {
            if (blockParticleOption.getState().is(DSBlocks.MIXED_SLAB.get())) {
                type = MixedDoubleSlabBlockHooks.getParticleForTopSlab(instance, pos).orElse(blockParticleOption);
            }
        }

        instance.addParticle(type, x, y, z, xSpeed, ySpeed, zSpeed);
    }

}
