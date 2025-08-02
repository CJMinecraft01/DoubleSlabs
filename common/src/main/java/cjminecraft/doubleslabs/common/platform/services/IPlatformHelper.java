package cjminecraft.doubleslabs.common.platform.services;

public interface IPlatformHelper {

    IPlatformPluginHelper getPluginHelper();

    IPlatformBlocks getBlocks();

    IPlatformBlockEntities getBlockEntities();

    IPlatformItems getItems();

    IPlatformRecipes getRecipes();

}