package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import cjminecraft.doubleslabs.common.util.registry.SimpleRegistrar;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DSItems {

    public static final SimpleRegistrar<Item> ITEMS = new SimpleRegistrar<>(ForgeRegistries.ITEMS, DoubleSlabs.MODID);

    public static final VerticalSlabItem VERTICAL_SLAB = ITEMS.register("vertical_slab", new VerticalSlabItem());

}
