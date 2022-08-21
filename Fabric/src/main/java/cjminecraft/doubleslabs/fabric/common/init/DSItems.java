package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.IItems;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import cjminecraft.doubleslabs.fabric.common.item.FabricVerticalSlabItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class DSItems implements IItems {

    public static final DSItems INSTANCE = new DSItems();

    public static final VerticalSlabItem VERTICAL_SLAB = Registry.register(
            Registry.ITEM,
            new ResourceLocation(Constants.MODID, "vertical_slab"),
            new FabricVerticalSlabItem()
    );

    @Override
    public VerticalSlabItem getVerticalSlabItem() {
        return VERTICAL_SLAB;
    }
}
