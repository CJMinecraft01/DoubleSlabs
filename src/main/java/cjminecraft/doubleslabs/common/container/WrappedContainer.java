package cjminecraft.doubleslabs.common.container;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.PlayerEntityWrapper;
import cjminecraft.doubleslabs.api.ServerPlayerEntityWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class WrappedContainer extends Container {

    public final Container wrapped;
    public final World world;
    private final boolean positive;
    public final EntityPlayer player;
    public final ModContainer mod;
    public final int id;
    public final IBlockInfo blockInfo;

    public WrappedContainer(EntityPlayer player, Object mod, int id, IBlockInfo blockInfo, int x, int y, int z) {
        this.positive = blockInfo.isPositive();
        this.world = blockInfo.getWorld();
        this.blockInfo = blockInfo;
        this.mod = FMLCommonHandler.instance().findContainerFor(mod);
        this.id = id;
        this.player = player instanceof EntityPlayerMP ? new ServerPlayerEntityWrapper((EntityPlayerMP) player, (WorldServer) blockInfo.getWorld()) : new PlayerEntityWrapper(player, blockInfo.getWorld());
        this.wrapped = player instanceof EntityPlayerMP ? NetworkRegistry.INSTANCE.getRemoteGuiContainer(this.mod, (EntityPlayerMP) this.player, id, blockInfo.getWorld(), x, y, z) : null;
    }

//    public WrappedContainer(InventoryPlayer playerInventory, PacketBuffer buffer) {
//        BlockPos pos = buffer.readBlockPos();
//        this.positive = buffer.readBoolean();
//        SlabTileEntity tile = ((SlabTileEntity)playerInventory.player.world.getTileEntity(pos));
//        this.world = this.positive ? tile.getPositiveBlockInfo().getWorld() : tile.getNegativeBlockInfo().getWorld();
//        PlayerInventoryWrapper playerInventoryWrapper = new PlayerInventoryWrapper(playerInventory, this.world);
//        this.wrapped = ForgeRegistries.CONTAINERS.getValue(buffer.readResourceLocation()).create(id, playerInventoryWrapper, buffer);
////        this.wrapped = Registry.MENU.getByValue(buffer.readInt()).create(id, new PlayerInventoryWrapper(playerInventory, world), buffer);
//    }

    public boolean isPositive() {
        return this.positive;
    }

    private Optional<Container> getContainer() {
        return this.wrapped != null ? Optional.of(this.wrapped) : Optional.empty();
    }

    @Override
    public void addListener(IContainerListener listener) {
        if (getContainer().isPresent())
            getContainer().get().addListener(listener);
        else
            super.addListener(listener);
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return getContainer().map(Container::getInventory).orElseGet(super::getInventory);
    }

    @Override
    public void removeListener(IContainerListener listener) {
        if (getContainer().isPresent())
            getContainer().get().removeListener(listener);
        else
            super.removeListener(listener);
    }

    @Override
    public void detectAndSendChanges() {
        if (getContainer().isPresent())
            getContainer().get().detectAndSendChanges();
        else
            super.detectAndSendChanges();
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        return getContainer().map(c -> c.enchantItem(playerIn, id)).orElseGet(() -> super.enchantItem(playerIn, id));
    }

    @Nullable
    @Override
    public Slot getSlotFromInventory(IInventory inv, int slotIn) {
        return getContainer().map(c -> c.getSlotFromInventory(inv, slotIn)).orElseGet(() -> super.getSlotFromInventory(inv, slotIn));
    }

    @Override
    public Slot getSlot(int slotId) {
        return getContainer().map(c -> c.getSlot(slotId)).orElseGet(() -> super.getSlot(slotId));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return getContainer().map(c -> c.transferStackInSlot(playerIn, index)).orElseGet(() -> super.transferStackInSlot(playerIn, index));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return getContainer().map(c -> c.slotClick(slotId, dragType, clickTypeIn, player)).orElseGet(() -> super.slotClick(slotId, dragType, clickTypeIn, player));
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return getContainer().map(c -> c.canMergeSlot(stack, slotIn)).orElseGet(() -> super.canMergeSlot(stack, slotIn));
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        if (getContainer().isPresent())
            getContainer().get().onContainerClosed(playerIn);
        else
            super.onContainerClosed(playerIn);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (getContainer().isPresent())
            getContainer().get().onCraftMatrixChanged(inventoryIn);
        else
            super.onCraftMatrixChanged(inventoryIn);
    }

    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {
        if (getContainer().isPresent())
            getContainer().get().putStackInSlot(slotID, stack);
        else
            super.putStackInSlot(slotID, stack);
    }

    @Override
    public void setAll(List<ItemStack> stacks) {
        if (getContainer().isPresent())
            getContainer().get().setAll(stacks);
        else
            super.setAll(stacks);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (getContainer().isPresent())
            getContainer().get().updateProgressBar(id, data);
        else
            super.updateProgressBar(id, data);
    }

    @Override
    public short getNextTransactionID(InventoryPlayer invPlayer) {
        return getContainer().map(c -> c.getNextTransactionID(invPlayer)).orElseGet(() -> super.getNextTransactionID(invPlayer));
    }

    @Override
    public boolean getCanCraft(EntityPlayer player) {
        return getContainer().map(c -> c.getCanCraft(player)).orElseGet(() -> super.getCanCraft(player));
    }

    @Override
    public void setCanCraft(EntityPlayer player, boolean canCraft) {
        if (getContainer().isPresent())
            getContainer().get().setCanCraft(player, canCraft);
        else
            super.setCanCraft(player, canCraft);
    }

    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
        return getContainer().map(c -> c.canDragIntoSlot(slotIn)).orElseGet(() -> super.canDragIntoSlot(slotIn));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return getContainer().map(c -> c.canInteractWith(playerIn)).orElse(true);
    }
}
