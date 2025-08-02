package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import cjminecraft.doubleslabs.common.platform.services.IPlatformItems;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeItems;
import net.minecraft.core.component.DataComponentType;

public class NeoForgeItems implements IPlatformItems {
    @Override
    public VerticalSlabItem getVerticalSlabItem() {
        return DSNeoForgeItems.VERTICAL_SLAB.get();
    }

    @Override
    public DataComponentType<VerticalSlabContent> getVerticalSlabContentDataComponentType() {
        return DSNeoForgeItems.VERTICAL_SLAB_CONTENT.get();
    }
}
