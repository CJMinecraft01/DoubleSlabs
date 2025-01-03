package cjminecraft.doubleslabs.fabric.common;

import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class DSFabricInit {

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, DSBlocks.MIXED_SLAB_ID, DSBlocks.MIXED_SLAB);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, DSBlockEntities.DYNAMIC_SLAB_ID, DSBlockEntities.DYNAMIC_SLAB);
    }

}
