package cjminecraft.doubleslabs.common.crafting;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.init.DSRecipes;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public record VerticalSlabRecipe(ResourceLocation id) implements CraftingRecipe {

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        int matches = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (!DSConfig.COMMON.isBlacklistedCraftingItem(stack.getItem()) && (SlabSupport.isHorizontalSlab(stack.getItem()) || stack.getItem() == DSItems.VERTICAL_SLAB.get())) {
                    if (stack.getItem() == DSItems.VERTICAL_SLAB.get() && DSConfig.COMMON.isBlacklistedCraftingItem(VerticalSlabItem.getStack(stack).getItem()))
                        continue;
                    matches++;
                } else
                    return false;
            }
        }
        return matches == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack itemStack = inv.getItem(i);
            if (!itemStack.isEmpty() && (SlabSupport.isHorizontalSlab(itemStack.getItem()) || itemStack.getItem() == DSItems.VERTICAL_SLAB.get()) && !DSConfig.COMMON.isBlacklistedCraftingItem(stack.getItem())) {
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
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DSRecipes.DYNAMIC_VERTICAL_SLAB.get();
    }
}
