package cjminecraft.doubleslabs.forge.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntityFactory;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class ForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new ForgePluginHelper();
    private final IPlatformBlockFactory blockFactory = new ForgeBlockFactory();
    private final IPlatformBlockEntityFactory blockEntityFactory = new ForgeBlockEntityFactory();

    @Override
    public IPlatformPluginHelper getPluginHelper() {
        return pluginHelper;
    }

    @Override
    public IPlatformBlockFactory getBlockFactory() {
        return blockFactory;
    }

    @Override
    public IPlatformBlockEntityFactory getBlockEntityFactory() {
        return blockEntityFactory;
    }
}