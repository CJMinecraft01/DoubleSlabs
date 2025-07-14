package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import static cjminecraft.doubleslabs.common.init.DSItems.*;

public class DSFabricItems {

    public static final VerticalSlabItem VERTICAL_SLAB = new VerticalSlabItem();
    public static final DataComponentType<VerticalSlabContent> VERTICAL_SLAB_CONTENT = DataComponentType.<VerticalSlabContent>builder().persistent(VerticalSlabContent.CODEC).networkSynchronized(VerticalSlabContent.STREAM_CODEC).build();

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, VERTICAL_SLAB_ID, VERTICAL_SLAB);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, VERTICAL_SLAB_CONTENT_ID, VERTICAL_SLAB_CONTENT);
    }

}
