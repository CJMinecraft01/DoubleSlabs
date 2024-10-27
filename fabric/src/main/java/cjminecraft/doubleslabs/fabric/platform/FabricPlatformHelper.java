package cjminecraft.doubleslabs.fabric.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class FabricPlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new FabricPluginHelper();

    @Override
    public IPlatformPluginHelper getPluginHelper() {
        return pluginHelper;
    }
}
