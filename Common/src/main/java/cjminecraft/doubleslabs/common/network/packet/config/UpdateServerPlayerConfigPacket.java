package cjminecraft.doubleslabs.common.network.packet.config;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.config.PlayerConfig;
import cjminecraft.doubleslabs.common.network.INetworkContext;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Supplier;

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

    public static void handle(UpdateServerPlayerConfigPacket message, Supplier<INetworkContext> ctxSupplier) {
        INetworkContext ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            IPlayerConfig config = Services.PLATFORM.getPlayerConfig(ctx.getSender());
            config.setVerticalSlabPlacementMethod(message.config.getVerticalSlabPlacementMethod());
            config.setPlaceVerticalSlabs(message.config.placeVerticalSlabs());
            Constants.LOG.debug("Received config update message from player %s: Placement Method - %s, Place Vertical Slab Keybinding Down - %s", ctx.getSender().getScoreboardName(), message.config.getVerticalSlabPlacementMethod(), message.config.placeVerticalSlabs());
        });
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.config.serializeNBT());
    }

}
