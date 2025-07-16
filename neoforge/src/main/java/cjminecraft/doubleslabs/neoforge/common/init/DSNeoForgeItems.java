package cjminecraft.doubleslabs.neoforge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.DSCreativeTabs;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static cjminecraft.doubleslabs.common.init.DSCreativeTabs.VERTICAL_SLABS_TAB_ID;
import static cjminecraft.doubleslabs.common.init.DSItems.*;

public class DSNeoForgeItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final DeferredHolder<Item, VerticalSlabItem> VERTICAL_SLAB = ITEMS.register(VERTICAL_SLAB_ID.getPath(), VerticalSlabItem::new);
    public static final Supplier<DataComponentType<VerticalSlabContent>> VERTICAL_SLAB_CONTENT = DATA_COMPONENTS.registerComponentType(VERTICAL_SLAB_CONTENT_ID.getPath(), builder -> builder.persistent(VerticalSlabContent.CODEC).networkSynchronized(VerticalSlabContent.STREAM_CODEC));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VERTICAL_SLABS_TAB = CREATIVE_MODE_TABS.register(VERTICAL_SLABS_TAB_ID.getPath(), () -> DSCreativeTabs.createVerticalSlabsTab(CreativeModeTab.builder(), BuiltInRegistries.ITEM));

}
