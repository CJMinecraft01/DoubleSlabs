package cjminecraft.doubleslabs.forge.common.proxy;

import net.minecraftforge.eventbus.api.IEventBus;

public interface IProxy {

    void addListeners(IEventBus mod, IEventBus forge);

}
