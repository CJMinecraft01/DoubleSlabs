package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntities;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlocks;
import cjminecraft.doubleslabs.common.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;

public class ForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new ForgePluginHelper();
    private final IPlatformBlocks blocks = new ForgeBlocks();
    private final IPlatformBlockEntities blockEntities = new ForgeBlockEntities();

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