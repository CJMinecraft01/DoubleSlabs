package cjminecraft.doubleslabs.fabric.common.network;

import cjminecraft.doubleslabs.common.network.INetworkContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class FabricClientNetworkContext implements INetworkContext {

    private final Minecraft client;
    private final ClientPacketListener handler;
    private final PacketSender responseSender;

    public FabricClientNetworkContext(Minecraft client, ClientPacketListener handler, PacketSender responseSender) {
        this.client = client;
        this.handler = handler;
        this.responseSender = responseSender;
    }

    @Override
    public @Nullable ServerPlayer getSender() {
        return null;
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        client.execute(runnable);
    }
}
