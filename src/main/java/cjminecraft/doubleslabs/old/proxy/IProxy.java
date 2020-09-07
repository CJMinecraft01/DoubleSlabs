package cjminecraft.doubleslabs.old.proxy;

import cjminecraft.doubleslabs.old.api.ContainerSupport;
import cjminecraft.doubleslabs.old.api.SlabSupport;
import cjminecraft.doubleslabs.old.network.PacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface IProxy {

    default void addListeners(IEventBus bus) {}

    default void setup(FMLCommonSetupEvent event) {
        SlabSupport.init();
        ContainerSupport.init();
        PacketHandler.registerPackets();
    }

    default void clientSetup(FMLClientSetupEvent event) {

    }

}
