package cjminecraft.doubleslabs.common.network;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.network.packet.config.RequestPlayerConfigPacket;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import cjminecraft.doubleslabs.old.network.PacketOpenGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1.1";
    public static SimpleChannel INSTANCE;

    private static int ID;

    private static int nextId() {
        return ID++;
    }

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DoubleSlabs.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
        // TODO re-add below message
        INSTANCE.registerMessage(nextId(), PacketOpenGui.class, PacketOpenGui::encode, PacketOpenGui::decode, PacketOpenGui::handle);
        INSTANCE.registerMessage(nextId(), RequestPlayerConfigPacket.class, RequestPlayerConfigPacket::encode, RequestPlayerConfigPacket::decode, RequestPlayerConfigPacket::handle);
        INSTANCE.registerMessage(nextId(), UpdateServerPlayerConfigPacket.class, UpdateServerPlayerConfigPacket::encode, UpdateServerPlayerConfigPacket::decode, UpdateServerPlayerConfigPacket::handle);
    }

}
