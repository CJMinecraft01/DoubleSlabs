package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntities;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlocks;
import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class FabricPlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new FabricPluginHelper();
    private final IPlatformBlocks blocks = new FabricBlocks();
    private final IPlatformBlockEntities blockEntities = new FabricBlockEntities();

    @Override
    public IPlatformPluginHelper getPluginHelper() {
        return pluginHelper;
    }

    @Override
    public IPlatformBlocks getBlocks() {
        return blocks;
    }

    @Override
    public IPlatformBlockEntities getBlockEntities() {
        return blockEntities;
    }
}
