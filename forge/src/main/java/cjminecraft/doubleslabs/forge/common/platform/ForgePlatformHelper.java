package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.platform.services.*;

public class ForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new ForgePluginHelper();
    private final IPlatformBlocks blocks = new ForgeBlocks();
    private final IPlatformBlockEntities blockEntities = new ForgeBlockEntities();
    private final IPlatformItems items = new ForgeItems();

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