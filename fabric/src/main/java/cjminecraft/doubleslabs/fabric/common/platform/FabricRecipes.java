package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.crafting.VerticalSlabConversionRecipe;
import cjminecraft.doubleslabs.common.platform.services.IPlatformRecipes;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricRecipes;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class FabricRecipes implements IPlatformRecipes {
    @Override
    public SimpleCraftingRecipeSerializer<VerticalSlabConversionRecipe> getVerticalSlabConversionRecipe() {
        return DSFabricRecipes.VERTICAL_SLAB_CONVERSION_SERIALIZER;
    }
}
