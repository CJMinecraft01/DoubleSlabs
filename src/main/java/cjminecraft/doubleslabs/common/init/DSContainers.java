package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.container.WrappedContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, DoubleSlabs.MODID);

    public static final RegistryObject<ContainerType<WrappedContainer>> WRAPPED_CONTAINER = CONTAINER_TYPES.register("wrapper", () -> IForgeContainerType.create(WrappedContainer::new));

}
