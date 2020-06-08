package cjminecraft.doubleslabs.proxy;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.network.PacketHandler;

public interface IProxy {
    default void preInit() {
        PacketHandler.registerMessages(DoubleSlabs.MODID);
    };
}
