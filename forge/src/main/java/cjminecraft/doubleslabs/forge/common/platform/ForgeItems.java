package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import cjminecraft.doubleslabs.common.platform.services.IPlatformItems;
import cjminecraft.doubleslabs.forge.common.init.DSForgeItems;
import net.minecraft.core.component.DataComponentType;

public class ForgeItems implements IPlatformItems {
    @Override
    public VerticalSlabItem getVerticalSlabItem() {
        return DSForgeItems.VERTICAL_SLAB.get();
    }

    @Override
    public DataComponentType<VerticalSlabContent> getVerticalSlabContentDataComponentType() {
        return DSForgeItems.VERTICAL_SLAB_CONTENT.get();
    }
}
