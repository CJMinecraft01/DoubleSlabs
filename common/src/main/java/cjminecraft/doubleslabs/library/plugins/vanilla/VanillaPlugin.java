package cjminecraft.doubleslabs.library.plugins.vanilla;

import cjminecraft.doubleslabs.api.DoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.registration.ISlabHelperRegistration;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.library.plugins.vanilla.helpers.MinecraftSlabHelper;
import net.minecraft.resources.ResourceLocation;

@DoubleSlabsPlugin
public class VanillaPlugin implements IDoubleSlabsPlugin {
    @Override
    public ResourceLocation getPluginId() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "minecraft");
    }

    @Override
    public void registerSlabHelpers(ISlabHelperRegistration registration) {
        registration.addHorizontalHelper(new MinecraftSlabHelper());
    }
}
