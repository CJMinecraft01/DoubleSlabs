package cjminecraft.doubleslabs.test.common.init;

import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.container.ChestSlabContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSTContainers {

    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, DoubleSlabsTest.MODID);

    public static final RegistryObject<MenuType<ChestSlabContainer>> CHEST_SLAB = CONTAINER_TYPES.register("chest_slab", () -> IForgeContainerType.create(ChestSlabContainer::new));

}
