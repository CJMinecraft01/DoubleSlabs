package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.platform.services.IPlatformItems;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeItems;

public class NeoForgeItems implements IPlatformItems {
    @Override
    public VerticalSlabItem getVerticalSlabItem() {
        return DSNeoForgeItems.VERTICAL_SLAB.get();
    }
}
