package cjminecraft.doubleslabs.neoforge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static cjminecraft.doubleslabs.common.init.DSItems.*;

public class DSNeoForgeItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(Constants.MOD_ID);

    public static final DeferredHolder<Item, VerticalSlabItem> VERTICAL_SLAB = ITEMS.register(VERTICAL_SLAB_ID.getPath(), VerticalSlabItem::new);

}
