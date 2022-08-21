package cjminecraft.doubleslabs.forge.common.menu;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.PlayerInventoryWrapper;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.common.menu.WrappedMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeWrappedMenu extends WrappedMenu {

    public ForgeWrappedMenu(int id, Inventory playerInventory, Player player, MenuProvider provider, IBlockInfo blockInfo) {
        super(id, playerInventory, player, provider, blockInfo);
    }

    public ForgeWrappedMenu(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        super(id);
        BlockPos pos = buffer.readBlockPos();
        this.positive = buffer.readBoolean();
        SlabBlockEntity<?> slab = ((SlabBlockEntity<?>)playerInventory.player.level.getBlockEntity(pos));
        assert slab != null;
        this.level = this.positive ? slab.getPositiveBlockInfo().getLevel() : slab.getNegativeBlockInfo().getLevel();
        this.wrapped = ForgeRegistries.MENU_TYPES.getValue(buffer.readResourceLocation()).create(id, new PlayerInventoryWrapper(playerInventory, level), buffer);
    }

}
