package cjminecraft.doubleslabs.api;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

public class PlayerInventoryWrapper extends Inventory {
    private final Inventory inv;
    
    public PlayerInventoryWrapper(Inventory inv, Level world) {
        super(new PlayerEntityWrapper(inv.player, world));
        this.inv = inv;
    }

    @Override
    public @NotNull ItemStack getSelected() {
        return this.inv.getSelected();
    }

    @Override
    public int getFreeSlot() {
        return this.inv.getFreeSlot();
    }

    @Override
    public void setPickedItem(@NotNull ItemStack stack) {
        this.inv.setPickedItem(stack);
    }

    @Override
    public void pickSlot(int slot) {
        this.inv.pickSlot(slot);
    }

    @Override
    public int findSlotMatchingItem(@NotNull ItemStack stack) {
        return this.inv.findSlotMatchingItem(stack);
    }

    @Override
    public int findSlotMatchingUnusedItem(@NotNull ItemStack stack) {
        return this.inv.findSlotMatchingUnusedItem(stack);
    }

    @Override
    public int getSuitableHotbarSlot() {
        return this.inv.getSuitableHotbarSlot();
    }

    @Override
    public void swapPaint(double paint) {
        this.inv.swapPaint(paint);
    }

    @Override
    public int clearOrCountMatchingItems(@NotNull Predicate<ItemStack> predicate, int i, @NotNull Container container) {
        return this.inv.clearOrCountMatchingItems(predicate, i, container);
    }

    @Override
    public int getSlotWithRemainingSpace(@NotNull ItemStack stack) {
        return this.inv.getSlotWithRemainingSpace(stack);
    }

    @Override
    public void tick() {
        this.inv.tick();
    }

    @Override
    public boolean add(@NotNull ItemStack stack) {
        return this.inv.add(stack);
    }

    @Override
    public boolean add(int slot, @NotNull ItemStack stack) {
        return this.inv.add(slot, stack);
    }

    @Override
    public void placeItemBackInInventory(@NotNull ItemStack stack) {
        this.inv.placeItemBackInInventory(stack);
    }

    @Override
    public void placeItemBackInInventory(@NotNull ItemStack stack, boolean b) {
        this.inv.placeItemBackInInventory(stack, b);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int count) {
        return this.inv.removeItem(slot, count);
    }

    @Override
    public void removeItem(@NotNull ItemStack stack) {
        this.inv.removeItem(stack);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return this.inv.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.inv.setItem(slot, stack);
    }

    @Override
    public float getDestroySpeed(@NotNull BlockState state) {
        return this.inv.getDestroySpeed(state);
    }

    @Override
    public @NotNull ListTag save(@NotNull ListTag tag) {
        return this.inv.save(tag);
    }

    @Override
    public void load(@NotNull ListTag tag) {
        this.inv.load(tag);
    }

    @Override
    public int getContainerSize() {
        return this.inv.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.inv.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.inv.getItem(slot);
    }

    @Override
    public @NotNull Component getName() {
        return this.inv.getName();
    }

    @Override
    public @NotNull ItemStack getArmor(int slot) {
        return this.inv.getArmor(slot);
    }

    @Override
    public void hurtArmor(@NotNull DamageSource source, float amount, int @NotNull [] weight) {
        this.inv.hurtArmor(source, amount, weight);
    }

    @Override
    public void dropAll() {
        this.inv.dropAll();
    }

    @Override
    public void setChanged() {
        this.inv.setChanged();
    }

    @Override
    public int getTimesChanged() {
        return this.inv.getTimesChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.inv.stillValid(player);
    }

    @Override
    public boolean contains(@NotNull ItemStack stack) {
        return this.inv.contains(stack);
    }

    @Override
    public boolean contains(@NotNull TagKey<Item> tag) {
        return this.inv.contains(tag);
    }

    @Override
    public void replaceWith(@NotNull Inventory inventory) {
        this.inv.replaceWith(inventory);
    }

    @Override
    public void clearContent() {
        this.inv.clearContent();
    }

    @Override
    public void fillStackedContents(@NotNull StackedContents contents) {
        this.inv.fillStackedContents(contents);
    }

    @Override
    public @NotNull ItemStack removeFromSelected(boolean b) {
        return this.inv.removeFromSelected(b);
    }

    @Override
    public int getMaxStackSize() {
        return this.inv.getMaxStackSize();
    }

    @Override
    public void startOpen(@NotNull Player player) {
        this.inv.startOpen(player);
    }

    @Override
    public void stopOpen(@NotNull Player player) {
        this.inv.stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return this.inv.canPlaceItem(slot, stack);
    }

    @Override
    public int countItem(@NotNull Item item) {
        return this.inv.countItem(item);
    }

    @Override
    public boolean hasAnyOf(@NotNull Set<Item> items) {
        return this.inv.hasAnyOf(items);
    }

    @Override
    public boolean hasCustomName() {
        return this.inv.hasCustomName();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.inv.getDisplayName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.inv.getCustomName();
    }
}
