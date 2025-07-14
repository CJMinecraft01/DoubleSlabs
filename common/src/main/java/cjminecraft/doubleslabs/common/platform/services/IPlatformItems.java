package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import net.minecraft.core.component.DataComponentType;

public interface IPlatformItems {

    VerticalSlabItem getVerticalSlabItem();

    DataComponentType<VerticalSlabContent> getVerticalSlabContentDataComponentType();

}
