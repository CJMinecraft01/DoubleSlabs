package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cjminecraft.doubleslabs.common.init.DSItems.*;

public class DSForgeItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);

    public static final RegistryObject<VerticalSlabItem> VERTICAL_SLAB = ITEMS.register(VERTICAL_SLAB_ID.getPath(), VerticalSlabItem::new);
    public static final RegistryObject<DataComponentType<VerticalSlabContent>> VERTICAL_SLAB_CONTENT = DATA_COMPONENT_TYPES.register(VERTICAL_SLAB_CONTENT_ID.getPath(), () -> DataComponentType.<VerticalSlabContent>builder().persistent(VerticalSlabContent.CODEC).networkSynchronized(VerticalSlabContent.STREAM_CODEC).build());

}
