package cjminecraft.doubleslabs.platform.services;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.init.IBlockEntities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.List;
import java.util.function.Consumer;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    void openScreen(Player player, MenuProvider provider, Consumer<FriendlyByteBuf> extraData);
    ResourceLocation getMenuTypeName(MenuType<?> type);

    double getReachDistance(Player player);

    LevelStorageSource.LevelStorageAccess getStorageFromServer(MinecraftServer server);

    IPlayerConfig getPlayerConfig(Player player);
}
