package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.HitResult;

public class MixedDoubleSlabBlockClientHooks extends MixedDoubleSlabBlockHooks {

    public static boolean addHitEffects(ClientLevel clientLevel, BlockPos slabPos, HitResult hitResult, Direction direction, ParticleEngine particleEngine) {
        return callOnLookingAtBlockState(clientLevel, slabPos, hitResult, state -> {
            ClientRenderingHooks.crackParticle(slabPos, state, direction, clientLevel, particleEngine);
            return true;
        }).orElse(false);
    }

}