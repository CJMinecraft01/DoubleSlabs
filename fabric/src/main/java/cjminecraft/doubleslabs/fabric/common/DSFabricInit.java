package cjminecraft.doubleslabs.fabric.common;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class DSFabricInit {

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, id("double_slab"), DSBlocks.MIXED_SLAB);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("dynamic_slab"), DSBlockEntities.DYNAMIC_SLAB);
    }

}
