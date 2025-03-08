package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.hooks.DynamicSlabBlockHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;

public class DynamicSlabBlockClientHooks extends DynamicSlabBlockHooks {

    public static BlockColor getBlockColour() {
        return (state, level, pos, tintIndex) -> {
            if (level == null || pos == null || tintIndex < 0) {
                return -1;
            }

            final var blockColours = Minecraft.getInstance().getBlockColors();

            return getDynamicSlabStateContainer(level, pos).flatMap(container -> {
                if (tintIndex > ClientConstants.TINT_OFFSET) {
                    return container.callOnBlockState(Half.TOP, slabState ->
                            blockColours.getColor(slabState, level, pos, tintIndex - ClientConstants.TINT_OFFSET));
                }
                return container.callOnBlockState(Half.BOTTOM, slabState ->
                                blockColours.getColor(slabState, level, pos, tintIndex));
            }).orElse(-1);
        };
    }

}
