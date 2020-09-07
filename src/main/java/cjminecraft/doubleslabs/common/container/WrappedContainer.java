package cjminecraft.doubleslabs.common.container;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.PlayerInventoryWrapper;
import cjminecraft.doubleslabs.common.init.DSContainers;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class WrappedContainer extends Container {

    public final Container wrapped;
    public final World world;
    private final boolean positive;

    public WrappedContainer(int id, PlayerInventory playerInventory, PlayerEntity player, INamedContainerProvider provider, IBlockInfo blockInfo) {
        super(DSContainers.WRAPPED_CONTAINER.get(), id);
        this.positive = blockInfo.isPositive();
        this.world = blockInfo.getWorld();
        this.wrapped = provider.createMenu(id, new PlayerInventoryWrapper(playerInventory, blockInfo.getWorld()), player);
    }

    public WrappedContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        super(DSContainers.WRAPPED_CONTAINER.get(), id);
        BlockPos pos = buffer.readBlockPos();
        this.positive = buffer.readBoolean();
        SlabTileEntity tile = ((SlabTileEntity)playerInventory.player.world.getTileEntity(pos));
        this.world = this.positive ? tile.getPositiveBlockInfo().getWorld() : tile.getNegativeBlockInfo().getWorld();
        this.wrapped = ForgeRegistries.CONTAINERS.getValue(buffer.readResourceLocation()).create(id, new PlayerInventoryWrapper(playerInventory, world), buffer);
//        this.wrapped = Registry.MENU.getByValue(buffer.readInt()).create(id, new PlayerInventoryWrapper(playerInventory, world), buffer);
    }

    public boolean isPositive() {
        return this.positive;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.wrapped.canInteractWith(playerIn);
    }

    @Override
    public void addListener(IContainerListener listener) {
        this.wrapped.addListener(listener);
    }

    @Override
    public void removeListener(IContainerListener listener) {
        this.wrapped.removeListener(listener);
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return this.wrapped.getInventory();
    }

    @Override
    public void detectAndSendChanges() {
        this.wrapped.detectAndSendChanges();
    }

    @Override
    public boolean enchantItem(PlayerEntity playerIn, int id) {
        return this.wrapped.enchantItem(playerIn, id);
    }

    @Override
    public Slot getSlot(int slotId) {
        return this.wrapped.getSlot(slotId);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return this.wrapped.transferStackInSlot(playerIn, index);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        return this.wrapped.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return this.wrapped.canMergeSlot(stack, slotIn);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        this.wrapped.onContainerClosed(playerIn);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.wrapped.onCraftMatrixChanged(inventoryIn);
    }

    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {
        this.wrapped.putStackInSlot(slotID, stack);
    }

    @Override
    public void setAll(List<ItemStack> stacks) {
        this.wrapped.setAll(stacks);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        this.wrapped.updateProgressBar(id, data);
    }

    @Override
    public short getNextTransactionID(PlayerInventory invPlayer) {
        return this.wrapped.getNextTransactionID(invPlayer);
    }

    @Override
    public boolean getCanCraft(PlayerEntity player) {
        return this.wrapped.getCanCraft(player);
    }

    @Override
    public void setCanCraft(PlayerEntity player, boolean canCraft) {
        this.wrapped.setCanCraft(player, canCraft);
    }

    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
        return this.wrapped.canDragIntoSlot(slotIn);
    }
}
