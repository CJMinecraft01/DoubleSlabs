package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.crafting.VerticalSlabRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSRecipes {

    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DoubleSlabs.MODID);

    public static final RegistryObject<SpecialRecipeSerializer<VerticalSlabRecipe>> DYNAMIC_VERTICAL_SLAB = RECIPE_SERIALIZERS.register("dynamic_vertical_slab", () -> new SpecialRecipeSerializer<>(VerticalSlabRecipe::new));

}
