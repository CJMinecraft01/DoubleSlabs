package cjminecraft.doubleslabs.common.network.packet.config;

import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RequestPlayerConfigPacket implements IMessage {

    public RequestPlayerConfigPacket() {
    }

    public void encode(PacketBuffer buf) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<RequestPlayerConfigPacket, UpdateServerPlayerConfigPacket> {

        @Override
        public UpdateServerPlayerConfigPacket onMessage(RequestPlayerConfigPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                if (Minecraft.getMinecraft().player == null)
                    return;
                IPlayerConfig config = Minecraft.getMinecraft().player.getCapability(PlayerConfigCapability.PLAYER_CONFIG, null);
                if (config == null)
                    return;
                // ensure the config capability is up to date
                config.setVerticalSlabPlacementMethod(DSConfig.CLIENT.verticalSlabPlacementMethod);
                PacketHandler.INSTANCE.sendToServer(new UpdateServerPlayerConfigPacket(config));
            });
            return null;
        }
    }
}
