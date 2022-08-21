package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.common.menu.WrappedMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public interface IMenuTypes {
    MenuType<? extends WrappedMenu> getWrappedMenu();

    WrappedMenu createWrappedMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player, @NotNull MenuProvider toWrap, @NotNull IBlockInfo blockInfo);
}
