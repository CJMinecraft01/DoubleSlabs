package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.common.crafting.VerticalSlabConversionRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public interface IPlatformRecipes {

    SimpleCraftingRecipeSerializer<VerticalSlabConversionRecipe> getVerticalSlabConversionRecipe();

}
