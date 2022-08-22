package cjminecraft.doubleslabs.common.network.packet.config;

import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.config.IPlayerConfig;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class RequestPlayerConfigPacket {

    public static RequestPlayerConfigPacket decode(FriendlyByteBuf buf) {
        return new RequestPlayerConfigPacket();
    }

    public static void handle(RequestPlayerConfigPacket message, Minecraft minecraft) {
        if (minecraft.player == null)
            return;
        IPlayerConfig config = Services.PLATFORM.getPlayerConfig(minecraft.player);
        // ensure the config capability is up to date
        config.setVerticalSlabPlacementMethod(DSConfig.CLIENT.verticalSlabPlacementMethod.get());

//        PacketHandler.INSTANCE.sendToServer(new UpdateServerPlayerConfigPacket(config));
    }

//    public static void handle(RequestPlayerConfigPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
//        NetworkEvent.Context ctx = ctxSupplier.get();
//        ctx.enqueueWork(() -> {
//            if (Minecraft.getInstance().player == null)
//                return;
//            Minecraft.getInstance().player.getCapability(PlayerConfigCapability.PLAYER_CONFIG)
//                    .ifPresent(config -> {
//                        // ensure the config capability is up to date
//                        config.setVerticalSlabPlacementMethod(DSConfig.CLIENT.verticalSlabPlacementMethod.get());
//                        PacketHandler.INSTANCE.sendToServer(new UpdateServerPlayerConfigPacket(config));
//                    });
//        });
//        ctx.setPacketHandled(true);
//    }

    public void encode(FriendlyByteBuf buf) {

    }

}
