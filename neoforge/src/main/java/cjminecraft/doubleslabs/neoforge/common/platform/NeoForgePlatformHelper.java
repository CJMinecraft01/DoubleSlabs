package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntityFactory;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new NeoForgePluginHelper();
    private final IPlatformBlockFactory blockFactory = new NeoForgeBlockFactory();
    private final IPlatformBlockEntityFactory blockEntityFactory = new NeoForgeBlockEntityFactory();

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