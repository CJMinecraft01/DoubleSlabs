package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.common.hooks.VerticalSlabBlockHooks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class VerticalSlabBlockClientHooks  extends VerticalSlabBlockHooks {

    public static boolean addHitEffects(ClientLevel clientLevel, BlockState verticalSlabBlockState, BlockPos slabPos, HitResult hitResult, Direction direction, ParticleEngine particleEngine) {
        return callOnLookingAtBlockState(clientLevel, verticalSlabBlockState, slabPos, hitResult, state -> {
            ClientRenderingHooks.crackParticle(slabPos, state, direction, clientLevel, particleEngine);
            return true;
        }).orElse(false);
    }

}
