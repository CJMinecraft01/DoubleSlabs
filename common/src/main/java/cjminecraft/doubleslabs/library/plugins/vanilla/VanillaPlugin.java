package cjminecraft.doubleslabs.library.plugins.vanilla;

import cjminecraft.doubleslabs.api.DoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.registration.ISlabHelperRegistration;
import cjminecraft.doubleslabs.library.plugins.vanilla.helpers.MinecraftSlabHelper;

@DoubleSlabsPlugin
public class VanillaPlugin implements IDoubleSlabsPlugin {
    @Override
    public void registerSlabHelpers(ISlabHelperRegistration registration) {
        registration.addHorizontalHelper(new MinecraftSlabHelper());
    }
}
