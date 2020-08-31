package cjminecraft.doubleslabs.common.crafting;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.init.DSRecipes;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class VerticalSlabRecipe extends SpecialRecipe {
    public VerticalSlabRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int matches = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            // TODO blacklist items
            if (!stack.isEmpty() && (SlabSupport.addVerticalSlabItem(stack.getItem()) || stack.getItem() == DSItems.VERTICAL_SLAB.get()))
                matches++;
        }
        return matches == 1;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (!itemStack.isEmpty() && (SlabSupport.addVerticalSlabItem(itemStack.getItem()) || itemStack.getItem() == DSItems.VERTICAL_SLAB.get())) {
                stack = itemStack;
                break;
            }
        }
        if (stack.isEmpty())
            return stack;
        if (stack.getItem() == DSItems.VERTICAL_SLAB.get())
            return VerticalSlabItem.getStack(stack);
        return VerticalSlabItem.setStack(new ItemStack(DSItems.VERTICAL_SLAB.get()), stack);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return DSRecipes.DYNAMIC_VERTICAL_SLAB.get();
    }
}
