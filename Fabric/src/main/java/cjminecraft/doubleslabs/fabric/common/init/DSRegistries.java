package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.Constants;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;

public class DSRegistries {

    public static WritableRegistry<IHorizontalSlabSupport> HORIZONTAL_SLAB_SUPPORTS;
    public static WritableRegistry<IVerticalSlabSupport> VERTICAL_SLAB_SUPPORTS;
    public static WritableRegistry<IContainerSupport> CONTAINER_SUPPORTS;

    public static void register() {
        HORIZONTAL_SLAB_SUPPORTS = FabricRegistryBuilder.createSimple(IHorizontalSlabSupport.class, new ResourceLocation(Constants.MODID, "horizontal_slab_support")).buildAndRegister();
        VERTICAL_SLAB_SUPPORTS = FabricRegistryBuilder.createSimple(IVerticalSlabSupport.class, new ResourceLocation(Constants.MODID, "vertical_slab_support")).buildAndRegister();
        FabricRegistryBuilder.createSimple(IContainerSupport.class, new ResourceLocation(Constants.MODID, "container_support")).buildAndRegister();

    }

}
