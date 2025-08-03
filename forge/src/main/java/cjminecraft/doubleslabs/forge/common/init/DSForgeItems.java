package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.DSCreativeTabs;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cjminecraft.doubleslabs.common.init.DSCreativeTabs.VERTICAL_SLABS_TAB_ID;
import static cjminecraft.doubleslabs.common.init.DSItems.*;

public class DSForgeItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final RegistryObject<VerticalSlabItem> VERTICAL_SLAB = ITEMS.register(VERTICAL_SLAB_ID.getPath(), VerticalSlabItem::new);
    public static final RegistryObject<CreativeModeTab> VERTICAL_SLABS_TAB = CREATIVE_MODE_TABS.register(VERTICAL_SLABS_TAB_ID.getPath(), () -> DSCreativeTabs.createVerticalSlabsTab(CreativeModeTab.builder(), ForgeRegistries.ITEMS));

}
