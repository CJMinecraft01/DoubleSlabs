package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;

public class VerticalSlabItemClientHooks {

    public static ItemColor getItemColour() {
        return (stack, tintIndex) -> {
            if (tintIndex < 0) {
                return -1;
            }

            final var slabStack = VerticalSlabItem.getContainedSlabItem(stack);

            final var itemColours = Minecraft.getInstance().itemColors;
            return itemColours.getColor(slabStack, tintIndex);
        };
    }

}
