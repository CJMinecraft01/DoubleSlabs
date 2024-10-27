package cjminecraft.doubleslabs.neoforge.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new NeoForgePluginHelper();

    @Override
    public IPlatformPluginHelper getPluginHelper() {
        return pluginHelper;
    }
}