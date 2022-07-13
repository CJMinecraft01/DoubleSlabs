package cjminecraft.doubleslabs.test.common.init;

import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.container.ChestSlabContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSTContainers {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DoubleSlabsTest.MODID);

    public static final RegistryObject<MenuType<ChestSlabContainer>> CHEST_SLAB = MENU_TYPES.register("chest_slab", () -> IForgeMenuType.create(ChestSlabContainer::new));

}
