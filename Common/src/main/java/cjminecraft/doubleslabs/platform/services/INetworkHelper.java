package cjminecraft.doubleslabs.platform.services;

import cjminecraft.doubleslabs.common.network.INetworkContext;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface INetworkHelper {

    <MSG> void registerMessage(int id, Class<MSG> messageClass, Function<FriendlyByteBuf, MSG> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<INetworkContext>> handler);

    <MSG> void sendToServer(MSG msg);

}
