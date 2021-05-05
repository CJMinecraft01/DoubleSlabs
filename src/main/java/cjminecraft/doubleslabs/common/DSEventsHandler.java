package cjminecraft.doubleslabs.common;

import cjminecraft.doubleslabs.api.capability.blockhalf.BlockHalfContainer;
import cjminecraft.doubleslabs.common.capability.DoubleSlabsCapabilities;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigContainer;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import cjminecraft.doubleslabs.common.network.packet.config.RequestPlayerConfigPacket;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class DSEventsHandler {

    @SubscribeEvent
    public static void attachConfigCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity)
            event.addCapability(DoubleSlabsCapabilities.PLAYER_CONFIG_RESOURCE_LOCATION, new PlayerConfigContainer());
    }

    @SubscribeEvent
    public static void attachBlockHalfCapability(AttachCapabilitiesEvent<TileEntity> event) {
        event.addCapability(DoubleSlabsCapabilities.BLOCK_HALF_RESOURCE_LOCATION, new BlockHalfContainer());
    }

    @SubscribeEvent
    public static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        event.getPlayer().getCapability(PlayerConfigCapability.PLAYER_CONFIG).ifPresent(config -> {
            // Called server side, need to fetch player options
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new RequestPlayerConfigPacket());
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void onFileChange(final ModConfig.Reloading event) {
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
