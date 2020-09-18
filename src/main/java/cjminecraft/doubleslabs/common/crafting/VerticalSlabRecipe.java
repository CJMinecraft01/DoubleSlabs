package cjminecraft.doubleslabs.common.crafting;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class VerticalSlabRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int matches = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (!DSConfig.SERVER.isBlacklistedCraftingItem(stack) && (SlabSupport.isHorizontalSlab(stack.getItem()) || stack.getItem() == DSItems.VERTICAL_SLAB)) {
                    if (stack.getItem() == DSItems.VERTICAL_SLAB && DSConfig.SERVER.isBlacklistedCraftingItem(VerticalSlabItem.getStack(stack)))
                        continue;
                    matches++;
                } else
                    return false;
            }
        }
        return matches == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (!itemStack.isEmpty() && (SlabSupport.isHorizontalSlab(itemStack.getItem()) || itemStack.getItem() == DSItems.VERTICAL_SLAB) && !DSConfig.SERVER.isBlacklistedCraftingItem(stack)) {
                stack = itemStack;
                break;
            }
        }
        if (stack.isEmpty())
            return stack;
        if (stack.getItem() == DSItems.VERTICAL_SLAB)
            return VerticalSlabItem.getStack(stack);
        return VerticalSlabItem.setStack(new ItemStack(DSItems.VERTICAL_SLAB), stack);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}
