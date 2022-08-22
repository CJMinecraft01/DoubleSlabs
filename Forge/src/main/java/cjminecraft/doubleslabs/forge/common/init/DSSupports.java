package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.api.support.minecraft.MinecraftSlabSupport;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber
public class DSSupports {

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(DSRegistries.HORIZONTAL_SLAB_SUPPORTS.getRegistryKey(), new ResourceLocation("minecraft:slab_support"), MinecraftSlabSupport::new);
    }

}
