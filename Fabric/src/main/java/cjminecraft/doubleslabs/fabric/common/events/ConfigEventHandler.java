package cjminecraft.doubleslabs.fabric.common.events;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.network.packet.config.RequestPlayerConfigPacket;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import cjminecraft.doubleslabs.platform.Services;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigEventHandler {

    public static void onPlayerJoin(ServerPlayer player) {
        Services.NETWORK.sendToPlayer(player, new RequestPlayerConfigPacket());
    }

    @Environment(EnvType.CLIENT)
    public static void onConfigChanged(final ModConfig config) {
        if (config.getModId().equals(Constants.MODID) && config.getType() == ModConfig.Type.CLIENT) {
            if (Minecraft.getInstance().player == null)
                return;
            IPlayerConfig playerConfig = Services.PLATFORM.getPlayerConfig(Minecraft.getInstance().player);
            if (playerConfig.getVerticalSlabPlacementMethod() != DSConfig.CLIENT.verticalSlabPlacementMethod.get()) {
                // Config has changed, update the server
                playerConfig.setVerticalSlabPlacementMethod(DSConfig.CLIENT.verticalSlabPlacementMethod.get());
                Services.NETWORK.sendToServer(new UpdateServerPlayerConfigPacket(playerConfig));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static void clientRegister() {
        ModConfigEvent.RELOADING.register(ConfigEventHandler::onConfigChanged);
    }

}
