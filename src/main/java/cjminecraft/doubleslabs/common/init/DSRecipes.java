package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.crafting.VerticalSlabRecipe;
import cjminecraft.doubleslabs.common.util.registry.SimpleRegistrar;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DSRecipes {

    public static final SimpleRegistrar<IRecipe> RECIPES = new SimpleRegistrar<>(ForgeRegistries.RECIPES, DoubleSlabs.MODID);

    public static final VerticalSlabRecipe DYNAMIC_VERTICAL_SLAB = RECIPES.register("dynamic_vertical_slab", new VerticalSlabRecipe());

}
