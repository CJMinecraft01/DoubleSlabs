package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.network.INetworkContext;
import cjminecraft.doubleslabs.common.network.NetworkDirection;
import cjminecraft.doubleslabs.forge.common.network.ForgeNetworkContext;
import cjminecraft.doubleslabs.platform.services.INetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeNetworkHelper implements INetworkHelper {

    private static final String PROTOCOL_VERSION = "1.2";

    private SimpleChannel INSTANCE;

    @Override
    public void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    }

    @Override
    public <MSG> void registerMessage(int id, Class<MSG> messageClass, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<INetworkContext>> handler) {
        INSTANCE.registerMessage(id, messageClass, encoder, decoder, (msg, ctxSupplier) -> {
            NetworkEvent.Context ctx = ctxSupplier.get();
            handler.accept(msg, () -> new ForgeNetworkContext(ctx));
            ctx.setPacketHandled(true);
        });
    }

    @Override
    public <MSG> void registerMessage(int id, Class<MSG> messageClass, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<INetworkContext>> handler, NetworkDirection direction) {
        INSTANCE.registerMessage(id, messageClass, encoder, decoder, (msg, ctxSupplier) -> {
            NetworkEvent.Context ctx = ctxSupplier.get();
            handler.accept(msg, () -> new ForgeNetworkContext(ctx));
            ctx.setPacketHandled(true);
        }, Optional.of(switch (direction) {
            case PLAY_TO_CLIENT -> net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
            case PLAY_TO_SERVER -> net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;
        }));
    }

    @Override
    public <MSG> void sendToServer(MSG msg) {
        INSTANCE.sendToServer(msg);
    }

    @Override
    public <MSG> void sendToLevelClients(Level level, MSG msg) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), msg);
    }
}
