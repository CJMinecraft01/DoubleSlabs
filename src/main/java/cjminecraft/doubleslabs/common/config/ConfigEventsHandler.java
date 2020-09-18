package cjminecraft.doubleslabs.common.config;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigContainer;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import cjminecraft.doubleslabs.common.network.packet.config.RequestPlayerConfigPacket;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class ConfigEventsHandler {

    @SubscribeEvent
    public static void attachConfigCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(PlayerConfigCapability.PLAYER_CONFIG_RESOURCE_LOCATION, new PlayerConfigContainer());
    }

    @SubscribeEvent
    public static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
//        IPlayerConfig config = event.player.getCapability(PlayerConfigCapability.PLAYER_CONFIG, null);
//        if (config != null)
            // Called server side, need to fetch player options
        PacketHandler.INSTANCE.sendTo(new RequestPlayerConfigPacket(), (EntityPlayerMP) event.player);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onFileChange(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DoubleSlabs.MODID)) {
            DSConfig.syncFromGui();
            if (Minecraft.getMinecraft().player == null)
                return;
            IPlayerConfig config = Minecraft.getMinecraft().player.getCapability(PlayerConfigCapability.PLAYER_CONFIG, null);
            if (config != null && config.getVerticalSlabPlacementMethod() != DSConfig.CLIENT.verticalSlabPlacementMethod) {
                // Config has changed, update the server
                config.setVerticalSlabPlacementMethod(DSConfig.CLIENT.verticalSlabPlacementMethod);
                PacketHandler.INSTANCE.sendToServer(new UpdateServerPlayerConfigPacket(config));
            }
        }
    }
}
