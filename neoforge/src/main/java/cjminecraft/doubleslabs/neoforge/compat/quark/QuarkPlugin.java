package cjminecraft.doubleslabs.neoforge.compat.quark;

import cjminecraft.doubleslabs.api.DoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.registration.ISlabHelperRegistration;
import cjminecraft.doubleslabs.common.Constants;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

@DoubleSlabsPlugin
public class QuarkPlugin implements IDoubleSlabsPlugin {
    @Override
    public ResourceLocation getPluginId() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "quark");
    }

    @Override
    public void registerSlabHelpers(ISlabHelperRegistration registration) {
        // Keep all references to Quark classes behind this optional integration.
        if (ModList.get().isLoaded("quark")) {
            registration.addVerticalHelper(new QuarkVerticalSlabHelper());
        }
    }
}
