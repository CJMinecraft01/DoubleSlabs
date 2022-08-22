package cjminecraft.doubleslabs.common.network.packet.config;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.config.PlayerConfig;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class UpdateServerPlayerConfigPacket {

    private final IPlayerConfig config;

    public UpdateServerPlayerConfigPacket(IPlayerConfig config) {
        this.config = config;
    }

    public static UpdateServerPlayerConfigPacket decode(FriendlyByteBuf buf) {
        PlayerConfig config = new PlayerConfig();
        config.deserializeNBT(Objects.requireNonNull(buf.readNbt()));
        return new UpdateServerPlayerConfigPacket(config);
    }

    public static void handle(UpdateServerPlayerConfigPacket message, Player player) {
        IPlayerConfig config = Services.PLATFORM.getPlayerConfig(player);
        config.setVerticalSlabPlacementMethod(message.config.getVerticalSlabPlacementMethod());
        config.setPlaceVerticalSlabs(message.config.placeVerticalSlabs());
        Constants.LOG.debug("Received config update message from player %s: Placement Method - %s, Place Vertical Slab Keybinding Down - %s", player.getScoreboardName(), message.config.getVerticalSlabPlacementMethod(), message.config.placeVerticalSlabs());
    }

//    public static void handle(UpdateServerPlayerConfigPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
//        NetworkEvent.Context ctx = ctxSupplier.get();
//        ctx.enqueueWork(() -> {
//            ctx.getSender().getCapability(PlayerConfigCapability.PLAYER_CONFIG).ifPresent(config -> {
//                config.setVerticalSlabPlacementMethod(message.config.getVerticalSlabPlacementMethod());
//                config.setPlaceVerticalSlabs(message.config.placeVerticalSlabs());
//            });
//            DoubleSlabs.LOGGER.debug("Received config update message from player %s: Placement Method - %s, Place Vertical Slab Keybinding Down - %s", ctx.getSender().getScoreboardName(), message.config.getVerticalSlabPlacementMethod(), message.config.placeVerticalSlabs());
//        });
//        ctx.setPacketHandled(true);
//    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.config.serializeNBT());
    }

}
