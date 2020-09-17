package cjminecraft.doubleslabs.common.network;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.network.packet.config.RequestPlayerConfigPacket;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper INSTANCE;

    private static int ID;

    private static int nextId() {
        return ID++;
    }

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DoubleSlabs.MODID);
        INSTANCE.registerMessage(RequestPlayerConfigPacket.Handler.class, RequestPlayerConfigPacket.class, nextId(), Side.CLIENT);
        INSTANCE.registerMessage(UpdateServerPlayerConfigPacket.Handler.class, UpdateServerPlayerConfigPacket.class, nextId(), Side.SERVER);
    }

}
