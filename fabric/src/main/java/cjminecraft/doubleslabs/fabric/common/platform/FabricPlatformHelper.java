package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.platform.services.*;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

public class FabricPlatformHelper implements IPlatformHelper {

    private static final double MAX_REACH_DISTANCE = Math.sqrt(ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE);

    private final IPlatformPluginHelper pluginHelper = new FabricPluginHelper();
    private final IPlatformBlocks blocks = new FabricBlocks();
    private final IPlatformBlockEntities blockEntities = new FabricBlockEntities();
    private final IPlatformItems items = new FabricItems();
    private final IPlatformRecipes recipes = new FabricRecipes();

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

    @Override
    public double getPlayerReachDistance(Player player) {
        return MAX_REACH_DISTANCE;
    }
}
