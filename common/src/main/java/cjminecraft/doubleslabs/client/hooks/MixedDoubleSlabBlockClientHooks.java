package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.HitResult;

import java.util.function.Function;

public class MixedDoubleSlabBlockClientHooks extends MixedDoubleSlabBlockHooks {

    public static boolean addHitEffects(ClientLevel clientLevel, BlockPos slabPos, HitResult hitResult, Direction direction, ParticleEngine particleEngine) {
        return callOnLookingAtBlockState(clientLevel, slabPos, hitResult, state -> {
            ClientRenderingHooks.crackParticle(slabPos, state, direction, clientLevel, particleEngine);
            return true;
        }).orElse(false);
    }

    public static boolean addDestroyEffects(BlockGetter blockGetter, BlockPos slabPos, ParticleEngine particleEngine) {
        return getDynamicSlabStateContainer(blockGetter, slabPos).map(container -> {
            container.runOnBlockStates(state -> particleEngine.destroy(slabPos, state));
            return true;
        }).orElse(false);
    }

    public static Function<BakedQuad, BakedQuad> withCorrectTint(Half half) {
        return switch (half) {
            case POSITIVE -> (quad) -> quad.isTinted() ?
                    new BakedQuad(quad.getVertices(), quad.getTintIndex() + ClientInternal.TINT_OFFSET, quad.getDirection(), quad.getSprite(), quad.isShade())
                    : quad;
            case NEGATIVE -> (quad) -> quad;
        };
    }

}