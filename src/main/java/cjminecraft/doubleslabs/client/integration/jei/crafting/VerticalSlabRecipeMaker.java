package cjminecraft.doubleslabs.client.integration.jei.crafting;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class VerticalSlabRecipeMaker {

    public static List<IRecipe<?>> createVerticalSlabRecipes() {
        List<IRecipe<?>> recipes = new ArrayList<>();
        String group = "doubleslabs.verticalslab";
        ForgeRegistries.ITEMS.forEach(item -> {
            if (SlabSupport.isHorizontalSlab(item)) {
                ItemStack slab = item.getDefaultInstance();
                ItemStack verticalSlab = VerticalSlabItem.setStack(new ItemStack(DSItems.VERTICAL_SLAB.get()), slab);

                recipes.add(new ShapelessRecipe(new ResourceLocation(DoubleSlabs.MODID, slab.getTranslationKey()), group, slab, NonNullList.from(Ingredient.EMPTY, Ingredient.fromStacks(verticalSlab))));
                recipes.add(new ShapelessRecipe(new ResourceLocation(DoubleSlabs.MODID, verticalSlab.getTranslationKey()), group, verticalSlab, NonNullList.from(Ingredient.EMPTY, Ingredient.fromStacks(slab))));
            }
        });
        return recipes;
    }

    private VerticalSlabRecipeMaker() {

    }

}
