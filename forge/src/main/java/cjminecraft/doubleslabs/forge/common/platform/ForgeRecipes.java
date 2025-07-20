package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.crafting.VerticalSlabConversionRecipe;
import cjminecraft.doubleslabs.common.platform.services.IPlatformRecipes;
import cjminecraft.doubleslabs.forge.common.init.DSForgeRecipes;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ForgeRecipes implements IPlatformRecipes {
    @Override
    public SimpleCraftingRecipeSerializer<VerticalSlabConversionRecipe> getVerticalSlabConversionRecipe() {
        return DSForgeRecipes.VERTICAL_SLAB_CONVERSION_SERIALIZER.get();
    }
}
