package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.IMenuTypes;
import cjminecraft.doubleslabs.common.menu.WrappedMenu;
import cjminecraft.doubleslabs.forge.common.menu.ForgeWrappedMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class DSMenuTypes implements IMenuTypes {

    public static final DSMenuTypes INSTANCE = new DSMenuTypes();

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MODID);

    public static final RegistryObject<MenuType<ForgeWrappedMenu>> WRAPPED = MENU_TYPES.register("wrapper", () -> IForgeMenuType.create(ForgeWrappedMenu::new));

    @Override
    public MenuType<? extends WrappedMenu> getWrappedMenu() {
        return WRAPPED.get();
    }

    @Override
    public WrappedMenu createWrappedMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player, @NotNull MenuProvider toWrap, @NotNull IBlockInfo blockInfo) {
        return new ForgeWrappedMenu(windowId, inventory, player, toWrap, blockInfo);
    }
}
