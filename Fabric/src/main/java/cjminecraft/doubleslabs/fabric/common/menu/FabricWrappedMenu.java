package cjminecraft.doubleslabs.fabric.common.menu;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.PlayerInventoryWrapper;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.common.menu.WrappedMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class FabricWrappedMenu extends WrappedMenu {

    public FabricWrappedMenu(int id, Inventory playerInventory, Player player, MenuProvider provider, IBlockInfo blockInfo) {
        super(id, playerInventory, player, provider, blockInfo);
    }

    public FabricWrappedMenu(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        super(id);
        BlockPos pos = buffer.readBlockPos();
        this.positive = buffer.readBoolean();
        SlabBlockEntity<?> slab = ((SlabBlockEntity<?>)playerInventory.player.level.getBlockEntity(pos));
        assert slab != null;
        this.level = this.positive ? slab.getPositiveBlockInfo().getLevel() : slab.getNegativeBlockInfo().getLevel();
        MenuType<?> menu = Registry.MENU.get(buffer.readResourceLocation());
        assert menu != null;
        if (menu instanceof ExtendedScreenHandlerType<?> extendedMenu) {
            this.wrapped = extendedMenu.create(id, new PlayerInventoryWrapper(playerInventory, level), buffer);
        } else {
            this.wrapped = menu.create(id, new PlayerInventoryWrapper(playerInventory, level));
        }
    }
}
