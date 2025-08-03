package cjminecraft.doubleslabs.common.platform.services;

import net.minecraft.world.entity.player.Player;

public interface IPlatformHelper {

    IPlatformPluginHelper getPluginHelper();

    IPlatformBlocks getBlocks();

    IPlatformBlockEntities getBlockEntities();

    IPlatformItems getItems();

    IPlatformRecipes getRecipes();

    double getPlayerReachDistance(Player player);

}