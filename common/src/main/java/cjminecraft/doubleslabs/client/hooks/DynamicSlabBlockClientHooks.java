package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.common.hooks.DynamicSlabBlockHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import java.util.function.Function;

public class DynamicSlabBlockClientHooks extends DynamicSlabBlockHooks {

    public static BlockColor getBlockColour() {
        return (state, level, pos, tintIndex) -> {
            if (level == null || pos == null || tintIndex < 0) {
                return -1;
            }

            final var blockColours = Minecraft.getInstance().getBlockColors();

            return getDynamicSlabStateContainer(level, pos).flatMap(container -> {
                if (tintIndex >= ClientInternal.TINT_OFFSET) {
                    return container.callOnBlockState(Half.POSITIVE, slabState ->
                            blockColours.getColor(slabState, level, pos, tintIndex - ClientInternal.TINT_OFFSET));
                }
                return container.callOnBlockState(Half.NEGATIVE, slabState ->
                                blockColours.getColor(slabState, level, pos, tintIndex));
            }).orElse(-1);
        };
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
