package cjminecraft.doubleslabs.fabric.common.network;

import cjminecraft.doubleslabs.common.network.INetworkContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

public class FabricServerNetworkContext implements INetworkContext {

    private final MinecraftServer server;
    private final ServerPlayer player;
    private final ServerGamePacketListenerImpl handler;
    private final PacketSender responseSender;

    public FabricServerNetworkContext(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, PacketSender responseSender) {
        this.server = server;
        this.player = player;
        this.handler = handler;
        this.responseSender = responseSender;
    }

    @Override
    public @Nullable ServerPlayer getSender() {
        return player;
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        server.execute(runnable);
    }
}
