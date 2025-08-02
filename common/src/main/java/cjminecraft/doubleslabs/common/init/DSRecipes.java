package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.crafting.VerticalSlabConversionRecipe;
import cjminecraft.doubleslabs.common.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.function.Supplier;

import static cjminecraft.doubleslabs.common.Constants.id;

public class DSRecipes {

    public static final ResourceLocation VERTICAL_SLAB_CONVERSION_ID = id("vertical_slab_conversion");

    public static final Supplier<SimpleCraftingRecipeSerializer<VerticalSlabConversionRecipe>> VERTICAL_SLAB_CONVERSION_SERIALIZER = () -> Services.PLATFORM.getRecipes().getVerticalSlabConversionRecipe();

}
