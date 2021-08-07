package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.container.WrappedContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSContainers {

    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, DoubleSlabs.MODID);

    public static final RegistryObject<MenuType<WrappedContainer>> WRAPPED_CONTAINER = CONTAINER_TYPES.register("wrapper", () -> IForgeContainerType.create(WrappedContainer::new));

}
