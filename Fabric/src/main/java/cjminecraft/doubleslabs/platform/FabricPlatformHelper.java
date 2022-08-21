package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.init.IBlockEntities;
import cjminecraft.doubleslabs.fabric.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.fabric.common.init.DSRegistries;
import cjminecraft.doubleslabs.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public List<IHorizontalSlabSupport> getHorizontalSlabSupports() {
        return DSRegistries.HORIZONTAL_SLAB_SUPPORTS.entrySet().stream().filter(e -> isModLoaded(e.getKey().location().getNamespace())).map(Map.Entry::getValue).toList();
    }

    @Override
    public List<IVerticalSlabSupport> getVerticalSlabSupports() {
        return DSRegistries.VERTICAL_SLAB_SUPPORTS.entrySet().stream().filter(e -> isModLoaded(e.getKey().location().getNamespace())).map(Map.Entry::getValue).toList();
    }

    @Override
    public List<IContainerSupport> getContainerSupports() {
        return DSRegistries.CONTAINER_SUPPORTS.entrySet().stream().filter(e -> isModLoaded(e.getKey().location().getNamespace())).map(Map.Entry::getValue).toList();
    }

    @Override
    public void openScreen(Player player, MenuProvider provider, Consumer<FriendlyByteBuf> extraData) {
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                extraData.accept(buf);
                if (provider instanceof ExtendedScreenHandlerFactory extendedProvider)
                    extendedProvider.writeScreenOpeningData(player, buf);
            }

            @Override
            public Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return provider.createMenu(i, inventory, player);
            }
        });
    }

    @Override
    public ResourceLocation getMenuTypeName(MenuType<?> type) {
        return Registry.MENU.getKey(type);
    }

    @Override
    public double getReachDistance(Player player) {
        return player.isCreative() ? 5 : 4.5; // todo: account for reach distance
    }
}
