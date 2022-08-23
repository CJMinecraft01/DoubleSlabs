package cjminecraft.doubleslabs.forge.client.proxy;

import cjminecraft.doubleslabs.forge.common.events.ConfigEventsHandler;
import cjminecraft.doubleslabs.forge.common.proxy.IProxy;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientProxy implements IProxy {
    @Override
    public void addListeners(IEventBus mod, IEventBus forge) {
        mod.addListener(ConfigEventsHandler::onFileChange);
    }
}
