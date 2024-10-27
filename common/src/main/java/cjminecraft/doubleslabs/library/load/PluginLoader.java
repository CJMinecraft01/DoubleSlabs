package cjminecraft.doubleslabs.library.load;

import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.helpers.ISlabHelper;
import cjminecraft.doubleslabs.api.registration.ISlabHelperRegistration;
import cjminecraft.doubleslabs.library.helpers.SlabHelper;
import cjminecraft.doubleslabs.library.load.registration.SlabHelperRegistration;

import java.util.List;

public class PluginLoader {

    public static ISlabHelper registerSlabHelpers(List<IDoubleSlabsPlugin> plugins) {
        final SlabHelper slabHelper = new SlabHelper();
        final ISlabHelperRegistration helperRegistration = new SlabHelperRegistration(slabHelper);

        plugins.forEach(plugin -> plugin.registerSlabHelpers(helperRegistration));

        return slabHelper;
    }

}
