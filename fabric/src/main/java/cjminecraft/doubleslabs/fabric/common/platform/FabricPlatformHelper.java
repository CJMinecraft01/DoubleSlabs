package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntityFactory;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class FabricPlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new FabricPluginHelper();
    private final IPlatformBlockFactory blockFactory = new FabricBlockFactory();
    private final IPlatformBlockEntityFactory blockEntityFactory = new FabricBlockEntityFactory();

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
