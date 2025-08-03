package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.platform.services.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.Objects;

public class ForgePlatformHelper implements IPlatformHelper {

    private final IPlatformPluginHelper pluginHelper = new ForgePluginHelper();
    private final IPlatformBlocks blocks = new ForgeBlocks();
    private final IPlatformBlockEntities blockEntities = new ForgeBlockEntities();
    private final IPlatformItems items = new ForgeItems();
    private final IPlatformRecipes recipes = new ForgeRecipes();

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
        return Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_REACH.get())).getValue();
    }
}