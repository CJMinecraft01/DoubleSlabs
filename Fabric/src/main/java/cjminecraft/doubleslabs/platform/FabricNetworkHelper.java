package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.network.INetworkContext;
import cjminecraft.doubleslabs.common.network.NetworkDirection;
import cjminecraft.doubleslabs.fabric.common.network.FabricClientNetworkContext;
import cjminecraft.doubleslabs.fabric.common.network.FabricServerNetworkContext;
import cjminecraft.doubleslabs.platform.services.INetworkHelper;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricNetworkHelper implements INetworkHelper {

    private static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Constants.MODID, "main");
    private final Map<Object, MessageHandler<?>> handlers = Maps.newHashMap();

    @Override
    public void init() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_NAME, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            MessageHandler<?> msgHandler = handlers.get(id);
            if (msgHandler != null && msgHandler.direction.orElse(NetworkDirection.PLAY_TO_CLIENT) == NetworkDirection.PLAY_TO_CLIENT) {
                Object msg = msgHandler.decode(buf);
                msgHandler.handle(msg, () -> new FabricClientNetworkContext(client, handler, responseSender));
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_NAME, (server, player, handler, buf, responseSender) -> {
            int id = buf.readInt();
            MessageHandler<?> msgHandler = handlers.get(id);
            if (msgHandler != null && msgHandler.direction.orElse(NetworkDirection.PLAY_TO_SERVER) == NetworkDirection.PLAY_TO_SERVER) {
                Object msg = msgHandler.decode(buf);
                msgHandler.handle(msg, () -> new FabricServerNetworkContext(server, player, handler, responseSender));
            }
        });
    }

    @Override
    public <MSG> void registerMessage(int id, Class<MSG> messageClass, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<INetworkContext>> handler) {
        MessageHandler<MSG> msgHandler = new MessageHandler<>(id, messageClass, encoder, decoder, handler, Optional.empty());
        handlers.put(id, msgHandler);
        handlers.put(messageClass, msgHandler);
    }

    @Override
    public <MSG> void registerMessage(int id, Class<MSG> messageClass, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<INetworkContext>> handler, NetworkDirection direction) {
        MessageHandler<MSG> msgHandler = new MessageHandler<>(id, messageClass, encoder, decoder, handler, Optional.of(direction));
        handlers.put(id, msgHandler);
        handlers.put(messageClass, msgHandler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <MSG> void sendToServer(MSG msg) {
        MessageHandler<MSG> handler = (MessageHandler<MSG>) handlers.get(msg.getClass());
        if (handler != null) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(handler.id);
            handler.encode(msg, buf);
            ClientPlayNetworking.send(CHANNEL_NAME, buf);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <MSG> void sendToLevelClients(Level level, MSG msg) {
        MessageHandler<MSG> handler = (MessageHandler<MSG>) handlers.get(msg.getClass());
        if (handler != null) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(handler.id);
            handler.encode(msg, buf);
            Objects.requireNonNull(level.getServer()).getPlayerList().broadcastAll(ServerPlayNetworking.createS2CPacket(CHANNEL_NAME, buf));
        }
    }

    private record MessageHandler<MSG>(int id, Class<MSG> messageClass, BiConsumer<MSG, FriendlyByteBuf> encoder,
                                       Function<FriendlyByteBuf, MSG> decoder,
                                       BiConsumer<MSG, Supplier<INetworkContext>> handler,
                                       Optional<NetworkDirection> direction) {

        public void encode(MSG msg, FriendlyByteBuf buf) {
            encoder.accept(msg, buf);
        }

        public MSG decode(FriendlyByteBuf buf) {
            return decoder.apply(buf);
        }

        @SuppressWarnings("unchecked")
        public void handle(Object msg, Supplier<INetworkContext> ctxSupplier) {
            handler.accept((MSG) msg, ctxSupplier);
        }
    }
}
