package cjminecraft.doubleslabs.common.crafting;

import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.init.DSRecipes;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class VerticalSlabConversionRecipe extends CustomRecipe {

    private static final Ingredient VERTICAL_SLAB = Ingredient.of(DSItems.VERTICAL_SLAB.get());

    public VerticalSlabConversionRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private Optional<ItemStack> getMatch(CraftingContainer craftingInput) {
        if (craftingInput.getContainerSize() != 1) {
            return Optional.empty();
        }

        final var stack = craftingInput.getItem(0);

        return Internal.getSlabHelper().isHorizontalSlab(stack) || VERTICAL_SLAB.test(stack) ?
                Optional.of(stack) : Optional.empty();
    }

    @Override
    public boolean matches(CraftingContainer craftingInput, Level level) {
        return getMatch(craftingInput).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingInput, RegistryAccess registryAccess) {
        final var stack = craftingInput.getItem(0);

        if (VERTICAL_SLAB.test(stack)) {
            return VerticalSlabItem.getContainedSlabItem(stack).copy();
        } else {
            return VerticalSlabItem.of(stack);
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DSRecipes.VERTICAL_SLAB_CONVERSION_SERIALIZER.get();
    }
}
