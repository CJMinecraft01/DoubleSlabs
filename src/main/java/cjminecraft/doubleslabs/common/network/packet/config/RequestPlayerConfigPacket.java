package cjminecraft.doubleslabs.common.network.packet.config;

import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestPlayerConfigPacket {

    public static RequestPlayerConfigPacket decode(PacketBuffer buf) {
        return new RequestPlayerConfigPacket();
    }

    public static void handle(RequestPlayerConfigPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null)
                return;
            Minecraft.getInstance().player.getCapability(PlayerConfigCapability.PLAYER_CONFIG)
                    .ifPresent(config -> {
                        // ensure the config capability is up to date
                        config.setVerticalSlabPlacementMethod(DSConfig.CLIENT.verticalSlabPlacementMethod.get());
                        PacketHandler.INSTANCE.sendToServer(new UpdateServerPlayerConfigPacket(config));
                    });
        });
        ctx.setPacketHandled(true);
    }

    public void encode(PacketBuffer buf) {

    }

}
