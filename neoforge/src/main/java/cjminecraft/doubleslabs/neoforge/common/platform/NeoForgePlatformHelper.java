package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.platform.services.*;

public class NeoForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new NeoForgePluginHelper();
    private final IPlatformBlocks blocks = new NeoForgeBlocks();
    private final IPlatformBlockEntities blockEntities = new NeoForgeBlockEntities();
    private final IPlatformItems items = new NeoForgeItems();
    private final IPlatformRecipes recipes = new NeoForgeRecipes();

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

    @Override
    public IPlatformRecipes getRecipes() {
        return recipes;
    }
}