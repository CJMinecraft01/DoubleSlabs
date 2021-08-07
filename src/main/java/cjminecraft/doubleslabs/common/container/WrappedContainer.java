package cjminecraft.doubleslabs.common.container;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.PlayerInventoryWrapper;
import cjminecraft.doubleslabs.common.init.DSContainers;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.OptionalInt;

public class WrappedContainer extends AbstractContainerMenu {

    public final AbstractContainerMenu wrapped;
    public final Level world;
    private final boolean positive;

    public WrappedContainer(int id, Inventory playerInventory, Player player, MenuProvider provider, IBlockInfo blockInfo) {
        super(DSContainers.WRAPPED_CONTAINER.get(), id);
        this.positive = blockInfo.isPositive();
        this.world = blockInfo.getWorld();
        this.wrapped = provider.createMenu(id, new PlayerInventoryWrapper(playerInventory, blockInfo.getWorld()), player);
    }

    public WrappedContainer(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        super(DSContainers.WRAPPED_CONTAINER.get(), id);
        BlockPos pos = buffer.readBlockPos();
        this.positive = buffer.readBoolean();
        SlabTileEntity tile = ((SlabTileEntity)playerInventory.player.level.getBlockEntity(pos));
        this.world = this.positive ? tile.getPositiveBlockInfo().getWorld() : tile.getNegativeBlockInfo().getWorld();
        this.wrapped = ForgeRegistries.CONTAINERS.getValue(buffer.readResourceLocation()).create(id, new PlayerInventoryWrapper(playerInventory, world), buffer);
//        this.wrapped = Registry.MENU.getByValue(buffer.readInt()).create(id, new PlayerInventoryWrapper(playerInventory, world), buffer);
    }

    public boolean isPositive() {
        return this.positive;
    }

    @Override
    public void addSlotListener(ContainerListener listener) {
        this.wrapped.addSlotListener(listener);
    }

    @Override
    public void setSynchronizer(ContainerSynchronizer synchronizer) {
        this.wrapped.setSynchronizer(synchronizer);
    }

    @Override
    public void sendAllDataToRemote() {
        this.wrapped.sendAllDataToRemote();
    }

    @Override
    public void removeSlotListener(ContainerListener listener) {
        this.wrapped.removeSlotListener(listener);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.wrapped.getItems();
    }

    @Override
    public void broadcastChanges() {
        this.wrapped.broadcastChanges();
    }

    @Override
    public void broadcastFullState() {
        this.wrapped.broadcastFullState();
    }

    @Override
    public void setRemoteSlot(int slot, ItemStack stack) {
        this.wrapped.setRemoteSlot(slot, stack);
    }

    @Override
    public void setRemoteSlotNoCopy(int slot, ItemStack stack) {
        this.wrapped.setRemoteSlotNoCopy(slot, stack);
    }

    @Override
    public void setRemoteCarried(ItemStack stack) {
        this.wrapped.setRemoteCarried(stack);
    }

    @Override
    public boolean clickMenuButton(Player player, int slot) {
        return this.wrapped.clickMenuButton(player, slot);
    }

    @Override
    public Slot getSlot(int slot) {
        return this.wrapped.getSlot(slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return this.quickMoveStack(player, slot);
    }

    @Override
    public void clicked(int x, int y, ClickType type, Player player) {
        this.wrapped.clicked(x, y, type, player);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return this.wrapped.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public void removed(Player player) {
        this.wrapped.removed(player);
    }

    @Override
    public void slotsChanged(Container container) {
        this.wrapped.slotsChanged(container);
    }

    @Override
    public void setItem(int slot, int state, ItemStack stack) {
        this.wrapped.setItem(slot, state, stack);
    }

    @Override
    public void initializeContents(int size, List<ItemStack> stacks, ItemStack stack) {
        this.wrapped.initializeContents(size, stacks, stack);
    }

    @Override
    public void setData(int a, int b) {
        this.wrapped.setData(a, b);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return this.wrapped.canDragTo(slot);
    }

    @Override
    public void setCarried(ItemStack stack) {
        this.wrapped.setCarried(stack);
    }

    @Override
    public ItemStack getCarried() {
        return this.wrapped.getCarried();
    }

    @Override
    public void suppressRemoteUpdates() {
        this.wrapped.suppressRemoteUpdates();
    }

    @Override
    public void resumeRemoteUpdates() {
        this.wrapped.resumeRemoteUpdates();
    }

    @Override
    public void transferState(AbstractContainerMenu container) {
        this.wrapped.transferState(container);
    }

    @Override
    public OptionalInt findSlot(Container container, int slot) {
        return this.wrapped.findSlot(container, slot);
    }

    @Override
    public int getStateId() {
        return this.wrapped.getStateId();
    }

    @Override
    public int incrementStateId() {
        return this.wrapped.incrementStateId();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.wrapped.stillValid(player);
    }
}
