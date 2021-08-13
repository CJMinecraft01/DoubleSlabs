package cjminecraft.doubleslabs.client.integration.jei;

import cjminecraft.doubleslabs.client.integration.jei.crafting.VerticalSlabRecipeMaker;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.init.DSItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIDoubleSlabsPlugin implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(DSItems.VERTICAL_SLAB.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(VerticalSlabRecipeMaker.createVerticalSlabRecipes(), VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
//        registration.getCraftingCategory().addCategoryExtension(VerticalSlabRecipe.class, VerticalSlabRecipeWrapper::new);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DoubleSlabs.MODID, "main");
    }
}
