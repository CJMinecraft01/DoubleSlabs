package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import cjminecraft.doubleslabs.common.platform.services.IPlatformItems;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricItems;
import net.minecraft.core.component.DataComponentType;

public class FabricItems implements IPlatformItems {
    @Override
    public VerticalSlabItem getVerticalSlabItem() {
        return DSFabricItems.VERTICAL_SLAB;
    }

    @Override
    public DataComponentType<VerticalSlabContent> getVerticalSlabContentDataComponentType() {
        return DSFabricItems.VERTICAL_SLAB_CONTENT;
    }
}
