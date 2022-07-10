package cjminecraft.doubleslabs.common.network;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.network.packet.config.RequestPlayerConfigPacket;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import cjminecraft.doubleslabs.common.network.packet.modelrefresh.UpdateSlabPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1.1";
    public static SimpleChannel INSTANCE;

    private static int ID;

    private static int nextId() {
        return ID++;
    }

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DoubleSlabs.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
        INSTANCE.registerMessage(nextId(), RequestPlayerConfigPacket.class, RequestPlayerConfigPacket::encode, RequestPlayerConfigPacket::decode, RequestPlayerConfigPacket::handle);
        INSTANCE.registerMessage(nextId(), UpdateServerPlayerConfigPacket.class, UpdateServerPlayerConfigPacket::encode, UpdateServerPlayerConfigPacket::decode, UpdateServerPlayerConfigPacket::handle);

        INSTANCE.registerMessage(nextId(), UpdateSlabPacket.class, UpdateSlabPacket::encode, UpdateSlabPacket::decode, UpdateSlabPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
