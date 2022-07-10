package cjminecraft.doubleslabs.common.network.packet.config;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateServerPlayerConfigPacket {

    private final IPlayerConfig config;

    public UpdateServerPlayerConfigPacket(IPlayerConfig config) {
        this.config = config;
    }

    public static UpdateServerPlayerConfigPacket decode(FriendlyByteBuf buf) {
        PlayerConfig config = new PlayerConfig();
        config.deserializeNBT(buf.readNbt());
        return new UpdateServerPlayerConfigPacket(config);
    }

    public static void handle(UpdateServerPlayerConfigPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ctx.getSender().getCapability(PlayerConfigCapability.PLAYER_CONFIG).ifPresent(config -> {
                config.setVerticalSlabPlacementMethod(message.config.getVerticalSlabPlacementMethod());
                config.setPlaceVerticalSlabs(message.config.placeVerticalSlabs());
            });
            DoubleSlabs.LOGGER.debug("Received config update message from player %s: Placement Method - %s, Place Vertical Slab Keybinding Down - %s", ctx.getSender().getScoreboardName(), message.config.getVerticalSlabPlacementMethod(), message.config.placeVerticalSlabs());
        });
        ctx.setPacketHandled(true);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.config.serializeNBT());
    }

}
