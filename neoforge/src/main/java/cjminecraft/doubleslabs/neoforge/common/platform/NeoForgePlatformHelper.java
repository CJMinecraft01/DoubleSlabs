package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntities;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlocks;
import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new NeoForgePluginHelper();
    private final IPlatformBlocks blocks = new NeoForgeBlocks();
    private final IPlatformBlockEntities blockEntities = new NeoForgeBlockEntities();

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