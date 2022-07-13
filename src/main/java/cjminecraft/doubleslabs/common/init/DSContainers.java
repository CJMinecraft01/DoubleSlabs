package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.container.WrappedContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSContainers {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DoubleSlabs.MODID);

    public static final RegistryObject<MenuType<WrappedContainer>> WRAPPED_CONTAINER = MENU_TYPES.register("wrapper", () -> IForgeMenuType.create(WrappedContainer::new));

}
