package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.crafting.VerticalSlabConversionRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cjminecraft.doubleslabs.common.init.DSRecipes.VERTICAL_SLAB_CONVERSION_ID;

public class DSForgeRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MOD_ID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<VerticalSlabConversionRecipe>> VERTICAL_SLAB_CONVERSION_SERIALIZER = RECIPE_SERIALIZERS.register(VERTICAL_SLAB_CONVERSION_ID.getPath(), () -> new SimpleCraftingRecipeSerializer<>(VerticalSlabConversionRecipe::new));
}
