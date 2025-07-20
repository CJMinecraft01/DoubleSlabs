package cjminecraft.doubleslabs.neoforge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.crafting.VerticalSlabConversionRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static cjminecraft.doubleslabs.common.init.DSRecipes.VERTICAL_SLAB_CONVERSION_ID;

public class DSNeoForgeRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Constants.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<VerticalSlabConversionRecipe>> VERTICAL_SLAB_CONVERSION_SERIALIZER = RECIPE_SERIALIZERS.register(VERTICAL_SLAB_CONVERSION_ID.getPath(), () -> new SimpleCraftingRecipeSerializer<>(VerticalSlabConversionRecipe::new));

}
