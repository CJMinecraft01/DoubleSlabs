package cjminecraft.doubleslabs.common.menu;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.PlayerInventoryWrapper;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.OptionalInt;

public abstract class WrappedMenu extends AbstractContainerMenu {

    public AbstractContainerMenu wrapped;
    public Level level;
    protected boolean positive;

    public WrappedMenu(int id) {
        super(Services.REGISTRIES.getMenuTypes().getWrappedMenu(), id);
    }

    public WrappedMenu(int id, Inventory playerInventory, Player player, MenuProvider provider, IBlockInfo blockInfo) {
        this(id);
        this.positive = blockInfo.isPositive();
        this.level = blockInfo.getLevel();
        this.wrapped = provider.createMenu(id, new PlayerInventoryWrapper(playerInventory, blockInfo.getLevel()), player);
    }

    public boolean isPositive() {
        return positive;
    }

    @Override
    public void addSlotListener(@NotNull ContainerListener listener) {
        this.wrapped.addSlotListener(listener);
    }

    @Override
    public void setSynchronizer(@NotNull ContainerSynchronizer synchronizer) {
        this.wrapped.setSynchronizer(synchronizer);
    }

    @Override
    public void sendAllDataToRemote() {
        this.wrapped.sendAllDataToRemote();
    }

    @Override
    public void removeSlotListener(@NotNull ContainerListener listener) {
        this.wrapped.removeSlotListener(listener);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
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
    public void setRemoteSlot(int slot, @NotNull ItemStack stack) {
        this.wrapped.setRemoteSlot(slot, stack);
    }

    @Override
    public void setRemoteSlotNoCopy(int slot, @NotNull ItemStack stack) {
        this.wrapped.setRemoteSlotNoCopy(slot, stack);
    }

    @Override
    public void setRemoteCarried(@NotNull ItemStack stack) {
        this.wrapped.setRemoteCarried(stack);
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int slot) {
        return this.wrapped.clickMenuButton(player, slot);
    }

    @Override
    public @NotNull Slot getSlot(int slot) {
        return this.wrapped.getSlot(slot);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slot) {
        return this.wrapped.quickMoveStack(player, slot);
    }

    @Override
    public void clicked(int x, int y, @NotNull ClickType type, @NotNull Player player) {
        this.wrapped.clicked(x, y, type, player);
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, @NotNull Slot slot) {
        return this.wrapped.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public void removed(@NotNull Player player) {
        this.wrapped.removed(player);
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        this.wrapped.slotsChanged(container);
    }

    @Override
    public void setItem(int slot, int state, @NotNull ItemStack stack) {
        this.wrapped.setItem(slot, state, stack);
    }

    @Override
    public void initializeContents(int size, @NotNull List<ItemStack> stacks, @NotNull ItemStack stack) {
        this.wrapped.initializeContents(size, stacks, stack);
    }

    @Override
    public void setData(int a, int b) {
        this.wrapped.setData(a, b);
    }

    @Override
    public boolean canDragTo(@NotNull Slot slot) {
        return this.wrapped.canDragTo(slot);
    }

    @Override
    public void setCarried(@NotNull ItemStack stack) {
        this.wrapped.setCarried(stack);
    }

    @Override
    public @NotNull ItemStack getCarried() {
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
    public void transferState(@NotNull AbstractContainerMenu container) {
        this.wrapped.transferState(container);
    }

    @Override
    public @NotNull OptionalInt findSlot(@NotNull Container container, int slot) {
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
    public boolean stillValid(@NotNull Player player) {
        return this.wrapped.stillValid(player);
    }
}
