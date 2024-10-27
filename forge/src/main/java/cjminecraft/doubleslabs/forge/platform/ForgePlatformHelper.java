package cjminecraft.doubleslabs.forge.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class ForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new ForgePluginHelper();

    @Override
    public IPlatformPluginHelper getPluginHelper() {
        return pluginHelper;
    }
}