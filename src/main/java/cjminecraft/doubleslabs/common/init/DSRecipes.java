package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.crafting.VerticalSlabRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DoubleSlabs.MODID);

    public static final RegistryObject<SimpleRecipeSerializer<VerticalSlabRecipe>> DYNAMIC_VERTICAL_SLAB = RECIPE_SERIALIZERS.register("dynamic_vertical_slab", () -> new SimpleRecipeSerializer<>(VerticalSlabRecipe::new));

}
