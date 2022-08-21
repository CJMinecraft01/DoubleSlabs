package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

public class DSRegistries {

    public static final DeferredRegister<IHorizontalSlabSupport> HORIZONTAL_SLAB_SUPPORTS = DeferredRegister.create(new ResourceLocation(Constants.MODID, "horizontal_slab_support"), Constants.MODID);
    public static final DeferredRegister<IVerticalSlabSupport> VERTICAL_SLAB_SUPPORTS = DeferredRegister.create(new ResourceLocation(Constants.MODID, "vertical_slab_support"), Constants.MODID);
    public static final DeferredRegister<IContainerSupport> CONTAINER_SUPPORTS = DeferredRegister.create(new ResourceLocation(Constants.MODID, "container_support"), Constants.MODID);

    static {
        HORIZONTAL_SLAB_SUPPORTS.makeRegistry(RegistryBuilder::new);
        VERTICAL_SLAB_SUPPORTS.makeRegistry(RegistryBuilder::new);
        CONTAINER_SUPPORTS.makeRegistry(RegistryBuilder::new);
    }

}
