package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.crafting.VerticalSlabConversionRecipe;
import cjminecraft.doubleslabs.common.platform.services.IPlatformRecipes;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeRecipes;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class NeoForgeRecipes implements IPlatformRecipes {
    @Override
    public SimpleCraftingRecipeSerializer<VerticalSlabConversionRecipe> getVerticalSlabConversionRecipe() {
        return DSNeoForgeRecipes.VERTICAL_SLAB_CONVERSION_SERIALIZER.get();
    }
}
