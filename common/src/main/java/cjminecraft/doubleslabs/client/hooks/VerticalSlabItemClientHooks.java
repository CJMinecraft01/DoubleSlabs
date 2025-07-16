package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.common.init.DSItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;

public class VerticalSlabItemClientHooks {

    public static ItemColor getItemColour() {
        return (stack, tintIndex) -> {
            if (tintIndex < 0) {
                return -1;
            }

            final var content = stack.get(DSItems.VERTICAL_SLAB_CONTENT.get());

            if (content == null) {
                return -1;
            }

            final var itemColours = Minecraft.getInstance().itemColors;
            return itemColours.getColor(content.getItem(), tintIndex);
        };
    }

}
