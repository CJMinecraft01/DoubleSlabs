package cjminecraft.doubleslabs.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

public class PlayerInventoryWrapper extends InventoryPlayer {
    private final InventoryPlayer inv;
    
    public PlayerInventoryWrapper(InventoryPlayer inv, World world) {
        super(inv.player instanceof EntityPlayerMP ? new ServerPlayerEntityWrapper((EntityPlayerMP) inv.player, (WorldServer) world) : new PlayerEntityWrapper(inv.player, world));
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
    public int storeItemStack(ItemStack itemStackIn) {
        return this.inv.storeItemStack(itemStackIn);
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
    public ItemStack armorItemInSlot(int slotIn) {
        return this.inv.armorItemInSlot(slotIn);
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
    public boolean hasItemStack(ItemStack itemStackIn) {
        return this.inv.hasItemStack(itemStackIn);
    }

    @Override
    public void clear() {
        this.inv.clear();
    }

    @Override
    public int getInventoryStackLimit() {
        return this.inv.getInventoryStackLimit();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return this.inv.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean hasCustomName() {
        return this.inv.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.inv.getDisplayName();
    }

    @Override
    public void damageArmor(float damage) {
        this.inv.damageArmor(damage);
    }

    @Override
    public void changeCurrentItem(int direction) {
        this.inv.changeCurrentItem(direction);
    }

    @Override
    public int clearMatchingItems(@Nullable Item itemIn, int metadataIn, int removeCount, @Nullable NBTTagCompound itemNBT) {
        return this.inv.clearMatchingItems(itemIn, metadataIn, removeCount, itemNBT);
    }

    @Override
    public void decrementAnimations() {
        this.inv.decrementAnimations();
    }

    @Override
    public float getDestroySpeed(IBlockState state) {
        return this.inv.getDestroySpeed(state);
    }

    @Override
    public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
        return this.inv.writeToNBT(nbtTagListIn);
    }

    @Override
    public void readFromNBT(NBTTagList nbtTagListIn) {
        this.inv.readFromNBT(nbtTagListIn);
    }

    @Override
    public String getName() {
        return this.inv.getName();
    }

    @Override
    public boolean canHarvestBlock(IBlockState state) {
        return this.inv.canHarvestBlock(state);
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.inv.isUsableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        this.inv.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        this.inv.closeInventory(player);
    }

    @Override
    public void copyInventory(InventoryPlayer playerInventory) {
        this.inv.copyInventory(playerInventory);
    }

    @Override
    public int getField(int id) {
        return this.inv.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        this.inv.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return this.inv.getFieldCount();
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper, boolean p_194016_2_) {
        this.inv.fillStackedContents(helper, p_194016_2_);
    }
}
