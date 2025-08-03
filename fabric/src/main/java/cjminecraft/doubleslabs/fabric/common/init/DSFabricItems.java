package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.init.DSCreativeTabs;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

import static cjminecraft.doubleslabs.common.init.DSCreativeTabs.VERTICAL_SLABS_TAB_ID;
import static cjminecraft.doubleslabs.common.init.DSItems.*;

public class DSFabricItems {

    public static final VerticalSlabItem VERTICAL_SLAB = new VerticalSlabItem();
    public static final CreativeModeTab VERTICAL_SLABS_TAB = DSCreativeTabs.createVerticalSlabsTab(FabricItemGroup.builder(), BuiltInRegistries.ITEM);

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, VERTICAL_SLAB_ID, VERTICAL_SLAB);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, VERTICAL_SLABS_TAB_ID, VERTICAL_SLABS_TAB);
    }

}
