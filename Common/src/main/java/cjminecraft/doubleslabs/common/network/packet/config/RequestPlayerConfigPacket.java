package cjminecraft.doubleslabs.common.network.packet.config;

import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.network.INetworkContext;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class RequestPlayerConfigPacket {

    public static RequestPlayerConfigPacket decode(FriendlyByteBuf buf) {
        return new RequestPlayerConfigPacket();
    }

    public static void handle(RequestPlayerConfigPacket message, Supplier<INetworkContext> ctxSupplier) {
        INetworkContext ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null)
                return;
            IPlayerConfig config = Services.PLATFORM.getPlayerConfig(Minecraft.getInstance().player);
            // ensure the config capability is up to date
            config.setVerticalSlabPlacementMethod(DSConfig.CLIENT.verticalSlabPlacementMethod.get());

            Services.NETWORK.sendToServer(new UpdateServerPlayerConfigPacket(config));
        });
    }

    public void encode(FriendlyByteBuf buf) {

    }

}
