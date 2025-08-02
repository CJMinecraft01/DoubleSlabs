package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.crafting.VerticalSlabConversionRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import static cjminecraft.doubleslabs.common.init.DSRecipes.VERTICAL_SLAB_CONVERSION_ID;

public class DSFabricRecipes {

    public static final SimpleCraftingRecipeSerializer<VerticalSlabConversionRecipe> VERTICAL_SLAB_CONVERSION_SERIALIZER = new SimpleCraftingRecipeSerializer<>(VerticalSlabConversionRecipe::new);

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, VERTICAL_SLAB_CONVERSION_ID, VERTICAL_SLAB_CONVERSION_SERIALIZER);
    }

}
