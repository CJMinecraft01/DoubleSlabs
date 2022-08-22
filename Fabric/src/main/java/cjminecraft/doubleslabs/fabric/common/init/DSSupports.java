package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.api.support.minecraft.MinecraftSlabSupport;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class DSSupports {

    public static void register() {
        Registry.register(
                DSRegistries.HORIZONTAL_SLAB_SUPPORTS,
                new ResourceLocation("minecraft:slab_support"),
                new MinecraftSlabSupport()
        );
    }

}
