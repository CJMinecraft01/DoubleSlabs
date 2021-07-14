package cjminecraft.doubleslabs.common.proxy;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public interface IProxy {

    void addListeners(IEventBus mod, IEventBus forge);

    default void loadComplete(FMLLoadCompleteEvent event) {

    }

}
