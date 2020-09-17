package cjminecraft.doubleslabs.common.network.packet.config;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateServerPlayerConfigPacket implements IMessage {

    private IPlayerConfig config;

    public UpdateServerPlayerConfigPacket() {
    }

    public UpdateServerPlayerConfigPacket(IPlayerConfig config) {
        this.config = config;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.config = new PlayerConfig();
        this.config.deserializeNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.config.serializeNBT());
    }

    public static class Handler implements IMessageHandler<UpdateServerPlayerConfigPacket, IMessage> {

        @Override
        public IMessage onMessage(UpdateServerPlayerConfigPacket message, MessageContext ctx) {
            IPlayerConfig config = ctx.getServerHandler().player.getCapability(PlayerConfigCapability.PLAYER_CONFIG, null);
            if (config != null) {
                config.setVerticalSlabPlacementMethod(message.config.getVerticalSlabPlacementMethod());
                config.setPlaceVerticalSlabs(message.config.placeVerticalSlabs());
            }
            DoubleSlabs.LOGGER.debug("Received config update message from player %s: Placement Method - %s, Place Vertical Slab Keybinding Down - %s", ctx.getServerHandler().player.getName(), message.config.getVerticalSlabPlacementMethod(), message.config.placeVerticalSlabs());
            return null;
        }
    }
}
