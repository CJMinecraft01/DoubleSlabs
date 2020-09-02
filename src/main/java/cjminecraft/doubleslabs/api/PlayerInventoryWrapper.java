package cjminecraft.doubleslabs.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

public class PlayerInventoryWrapper extends PlayerInventory {
    private final PlayerInventory inv;
    
    public PlayerInventoryWrapper(PlayerInventory inv, World world) {
        super(new PlayerEntityWrapper(inv.player, world));
        this.inv = inv;
    }

    @Override
    public ItemStack getCurrentItem() {
        return this.inv.getCurrentItem();
    }

    @Override
    public int getFirstEmptyStack() {
        return this.inv.getFirstEmptyStack();
    }

    @Override
    public void setPickedItemStack(ItemStack stack) {
        this.inv.setPickedItemStack(stack);
    }

    @Override
    public void pickItem(int index) {
        this.inv.pickItem(index);
    }

    @Override
    public int getSlotFor(ItemStack stack) {
        return this.inv.getSlotFor(stack);
    }

    @Override
    public int findSlotMatchingUnusedItem(ItemStack p_194014_1_) {
        return this.inv.findSlotMatchingUnusedItem(p_194014_1_);
    }

    @Override
    public int getBestHotbarSlot() {
        return this.inv.getBestHotbarSlot();
    }

    @Override
    public void changeCurrentItem(double direction) {
        this.inv.changeCurrentItem(direction);
    }

    @Override
    public int func_234564_a_(Predicate<ItemStack> p_234564_1_, int p_234564_2_, IInventory p_234564_3_) {
        return this.inv.func_234564_a_(p_234564_1_, p_234564_2_, p_234564_3_);
    }

    @Override
    public int storeItemStack(ItemStack itemStackIn) {
        return this.inv.storeItemStack(itemStackIn);
    }

    @Override
    public void tick() {
        this.inv.tick();
    }

    @Override
    public boolean addItemStackToInventory(ItemStack itemStackIn) {
        return this.inv.addItemStackToInventory(itemStackIn);
    }

    @Override
    public boolean add(int slotIn, ItemStack stack) {
        return this.inv.add(slotIn, stack);
    }

    @Override
    public void placeItemBackInInventory(World worldIn, ItemStack stack) {
        this.inv.placeItemBackInInventory(worldIn, stack);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return this.inv.decrStackSize(index, count);
    }

    @Override
    public void deleteStack(ItemStack stack) {
        this.inv.deleteStack(stack);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return this.inv.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.inv.setInventorySlotContents(index, stack);
    }

    @Override
    public float getDestroySpeed(BlockState state) {
        return this.inv.getDestroySpeed(state);
    }

    @Override
    public ListNBT write(ListNBT nbtTagListIn) {
        return this.inv.write(nbtTagListIn);
    }

    @Override
    public void read(ListNBT nbtTagListIn) {
        this.inv.read(nbtTagListIn);
    }

    @Override
    public int getSizeInventory() {
        return this.inv.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return this.inv.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.inv.getStackInSlot(index);
    }

    @Override
    public ITextComponent getName() {
        return this.inv.getName();
    }

    @Override
    public ItemStack armorItemInSlot(int slotIn) {
        return this.inv.armorItemInSlot(slotIn);
    }

    @Override
    public void func_234563_a_(DamageSource p_234563_1_, float p_234563_2_) {
        this.inv.func_234563_a_(p_234563_1_, p_234563_2_);
    }

    @Override
    public void dropAllItems() {
        this.inv.dropAllItems();
    }

    @Override
    public void markDirty() {
        this.inv.markDirty();
    }

    @Override
    public int getTimesChanged() {
        return this.inv.getTimesChanged();
    }

    @Override
    public void setItemStack(ItemStack itemStackIn) {
        this.inv.setItemStack(itemStackIn);
    }

    @Override
    public ItemStack getItemStack() {
        return this.inv.getItemStack();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return this.inv.isUsableByPlayer(player);
    }

    @Override
    public boolean hasItemStack(ItemStack itemStackIn) {
        return this.inv.hasItemStack(itemStackIn);
    }

    @Override
    public boolean hasTag(ITag<Item> itemTag) {
        return this.inv.hasTag(itemTag);
    }

    @Override
    public void copyInventory(PlayerInventory playerInventory) {
        this.inv.copyInventory(playerInventory);
    }

    @Override
    public void clear() {
        this.inv.clear();
    }

    @Override
    public void accountStacks(RecipeItemHelper p_201571_1_) {
        this.inv.accountStacks(p_201571_1_);
    }

    @Override
    public int getInventoryStackLimit() {
        return this.inv.getInventoryStackLimit();
    }

    @Override
    public void openInventory(PlayerEntity player) {
        this.inv.openInventory(player);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        this.inv.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return this.inv.isItemValidForSlot(index, stack);
    }

    @Override
    public int count(Item itemIn) {
        return this.inv.count(itemIn);
    }

    @Override
    public boolean hasAny(Set<Item> set) {
        return this.inv.hasAny(set);
    }

    @Override
    public boolean hasCustomName() {
        return this.inv.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.inv.getDisplayName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return this.inv.getCustomName();
    }
}
