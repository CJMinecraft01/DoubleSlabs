package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.IItems;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSItems implements IItems {

    public static final DSItems INSTANCE = new DSItems();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);

    public static final RegistryObject<VerticalSlabItem> VERTICAL_SLAB = ITEMS.register("vertical_slab", VerticalSlabItem::new);

    @Override
    public VerticalSlabItem getVerticalSlabItem() {
        return VERTICAL_SLAB.get();
    }
}
