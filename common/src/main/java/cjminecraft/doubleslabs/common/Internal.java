package cjminecraft.doubleslabs.common;

import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.helpers.ISlabHelper;
import cjminecraft.doubleslabs.common.platform.Services;
import cjminecraft.doubleslabs.library.load.PluginLoader;
import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class Internal {

    @Nullable
    private static ISlabHelper slabHelper;

    public static ISlabHelper getSlabHelper() {
        Preconditions.checkState(slabHelper != null, "SlabHelper not initialized");
        return slabHelper;
    }

    public static void initialise() {
        List<IDoubleSlabsPlugin> plugins = Services.PLATFORM.getPluginHelper().getPlugins();

        Constants.LOG.info("Detected {} plugins: {}",
                plugins.size(),
                LogUtils.defer(() -> plugins.stream().map(IDoubleSlabsPlugin::getPluginId).map(ResourceLocation::toString).collect(Collectors.joining(", "))));

        slabHelper = PluginLoader.registerSlabHelpers(plugins);
    }

}
