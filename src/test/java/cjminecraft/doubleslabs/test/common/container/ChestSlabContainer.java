package cjminecraft.doubleslabs.test.common.container;

import cjminecraft.doubleslabs.test.common.init.DSTContainers;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ChestSlabContainer extends AbstractContainerMenu {
    private final ItemStackHandler inventory;

    public ChestSlabContainer(int id, Inventory inv) {
        this(id, inv, (ChestSlabTileEntity) null);
    }

    public ChestSlabContainer(int id, Inventory inv, FriendlyByteBuf data) {
        this(id, inv, ((ChestSlabTileEntity) inv.player.level.getBlockEntity(data.readBlockPos())));
    }

    public ChestSlabContainer(int id, Inventory inv, @Nullable ChestSlabTileEntity tile) {
        super(DSTContainers.CHEST_SLAB.get(), id);

        this.inventory = tile != null ? tile.getInventory() : new ItemStackHandler(9);

        for(int j = 0; j < 9; ++j) {
            this.addSlot(new SlotItemHandler(this.inventory, j, 8 + j * 18, 20){
                @Override
                public void setChanged() {
                    super.setChanged();
                    if (tile != null)
                        tile.setChanged();
                }
            });
        }

        for(int l = 0; l < 3; ++l) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inv, i1, 8 + i1 * 18, 109));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.moveItemStackTo(itemstack1, this.inventory.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
