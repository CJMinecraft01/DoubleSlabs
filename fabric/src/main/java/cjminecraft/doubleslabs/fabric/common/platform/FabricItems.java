package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.platform.services.IPlatformItems;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricItems;

public class FabricItems implements IPlatformItems {
    @Override
    public VerticalSlabItem getVerticalSlabItem() {
        return DSFabricItems.VERTICAL_SLAB;
    }
}
