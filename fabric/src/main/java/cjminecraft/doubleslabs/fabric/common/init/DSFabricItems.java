package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import static cjminecraft.doubleslabs.common.init.DSItems.*;

public class DSFabricItems {

    public static final VerticalSlabItem VERTICAL_SLAB = new VerticalSlabItem();

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, VERTICAL_SLAB_ID, VERTICAL_SLAB);
    }

}
