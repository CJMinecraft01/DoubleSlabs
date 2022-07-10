package cjminecraft.doubleslabs.common.config;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigContainer;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import cjminecraft.doubleslabs.common.network.packet.config.RequestPlayerConfigPacket;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class ConfigEventsHandler {

    @SubscribeEvent
    public static void attachConfigCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player)
            event.addCapability(PlayerConfigCapability.PLAYER_CONFIG_RESOURCE_LOCATION, new PlayerConfigContainer());
    }

    @SubscribeEvent
    public static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        event.getPlayer().getCapability(PlayerConfigCapability.PLAYER_CONFIG).ifPresent(config -> {
            // Called server side, need to fetch player options
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new RequestPlayerConfigPacket());
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void onFileChange(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(DoubleSlabs.MODID) && event.getConfig().getType() == ModConfig.Type.CLIENT) {
            if (Minecraft.getInstance().player == null)
                return;
            Minecraft.getInstance().player.getCapability(PlayerConfigCapability.PLAYER_CONFIG).ifPresent(config -> {
                if (config.getVerticalSlabPlacementMethod() != DSConfig.CLIENT.verticalSlabPlacementMethod.get()) {
                    // Config has changed, update the server
                    config.setVerticalSlabPlacementMethod(DSConfig.CLIENT.verticalSlabPlacementMethod.get());
                    PacketHandler.INSTANCE.sendToServer(new UpdateServerPlayerConfigPacket(config));
                }
            });
        }
    }
}
