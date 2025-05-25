package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.platform.services.*;

public class FabricPlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new FabricPluginHelper();
    private final IPlatformBlocks blocks = new FabricBlocks();
    private final IPlatformBlockEntities blockEntities = new FabricBlockEntities();
    private final IPlatformItems items = new FabricItems();

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

    @Override
    public IPlatformItems getItems() {
        return items;
    }
}
