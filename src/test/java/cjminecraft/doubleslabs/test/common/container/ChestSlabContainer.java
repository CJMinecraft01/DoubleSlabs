package cjminecraft.doubleslabs.test.common.container;

import cjminecraft.doubleslabs.test.common.init.DSTContainers;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ChestSlabContainer extends Container {
    private final ItemStackHandler inventory;

    public ChestSlabContainer(int id, PlayerInventory inv) {
        this(id, inv, (ChestSlabTileEntity) null);
    }

    public ChestSlabContainer(int id, PlayerInventory inv, PacketBuffer data) {
        this(id, inv, ((ChestSlabTileEntity) inv.player.world.getTileEntity(data.readBlockPos())));
    }

    public ChestSlabContainer(int id, PlayerInventory inv, @Nullable ChestSlabTileEntity tile) {
        super(DSTContainers.CHEST_SLAB.get(), id);

        this.inventory = tile != null ? tile.getInventory() : new ItemStackHandler(9);

        for(int j = 0; j < 9; ++j) {
            this.addSlot(new SlotItemHandler(this.inventory, j, 8 + j * 18, 20){
                @Override
                public void onSlotChanged() {
                    if (tile != null)
                        tile.markDirty();
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
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.mergeItemStack(itemstack1, this.inventory.getSlots(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
