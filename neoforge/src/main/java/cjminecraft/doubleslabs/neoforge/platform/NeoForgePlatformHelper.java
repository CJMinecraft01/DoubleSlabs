package cjminecraft.doubleslabs.neoforge.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new NeoForgePluginHelper();

    @Override
    public IPlatformPluginHelper getPluginHelper() {
        return pluginHelper;
    }
}