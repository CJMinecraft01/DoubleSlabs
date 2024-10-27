package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.registration.ISlabHelperRegistration;
import net.minecraft.resources.ResourceLocation;

public interface IDoubleSlabsPlugin {

    ResourceLocation getPluginId();

    void registerSlabHelpers(ISlabHelperRegistration registration);

}
