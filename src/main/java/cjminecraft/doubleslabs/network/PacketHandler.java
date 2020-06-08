package cjminecraft.doubleslabs.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper INSTANCE;

    private static int ID = 0;

    private static int nextId() {
        return ID++;
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

        INSTANCE.registerMessage(PacketOpenGui.Handler.class, PacketOpenGui.class, nextId(), Side.CLIENT);
    }

}
