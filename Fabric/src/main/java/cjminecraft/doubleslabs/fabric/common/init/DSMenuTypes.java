package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.IMenuTypes;
import cjminecraft.doubleslabs.common.menu.WrappedMenu;
import cjminecraft.doubleslabs.fabric.common.menu.FabricWrappedMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class DSMenuTypes implements IMenuTypes {

    public static final DSMenuTypes INSTANCE = new DSMenuTypes();

    public static final MenuType<FabricWrappedMenu> WRAPPED = Registry.register(
            Registry.MENU,
            new ResourceLocation(Constants.MODID, "wrapper"),
            new ExtendedScreenHandlerType<>(FabricWrappedMenu::new)
    );

    @Override
    public MenuType<? extends WrappedMenu> getWrappedMenu() {
        return WRAPPED;
    }

    @Override
    public WrappedMenu createWrappedMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player, @NotNull MenuProvider toWrap, @NotNull IBlockInfo blockInfo) {
        return new FabricWrappedMenu(windowId, inventory, player, toWrap, blockInfo);
    }
}
