package cjminecraft.doubleslabs.platform.services;

import cjminecraft.doubleslabs.common.network.INetworkContext;
import cjminecraft.doubleslabs.common.network.NetworkDirection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface INetworkHelper {

    void init();

    <MSG> void registerMessage(int id, Class<MSG> messageClass, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<INetworkContext>> handler);
    <MSG> void registerMessage(int id, Class<MSG> messageClass, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<INetworkContext>> handler, NetworkDirection direction);

    <MSG> void sendToServer(MSG msg);

    <MSG> void sendToLevelClients(Level level, MSG msg);

    <MSG> void sendToPlayer(ServerPlayer player, MSG msg);

}
