package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.platform.services.IPlatformHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public void openScreen(Player player, MenuProvider provider, Consumer<FriendlyByteBuf> extraData) {
        NetworkHooks.openScreen((ServerPlayer) player, provider, extraData);
    }

    @Override
    public ResourceLocation getMenuTypeName(MenuType<?> type) {
        return ForgeRegistries.MENU_TYPES.getKey(type);
    }

    @Override
    public double getReachDistance(Player player) {
        return player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
    }

    @Override
    public LevelStorageSource.LevelStorageAccess getStorageFromServer(MinecraftServer server) {
        return server.storageSource;
    }
}
