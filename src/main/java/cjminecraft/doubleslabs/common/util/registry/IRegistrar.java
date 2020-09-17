package cjminecraft.doubleslabs.common.util.registry;

import net.minecraftforge.fml.common.eventhandler.EventBus;

public interface IRegistrar<T>{

    <V extends T> V register(String name, V object);

    void register(EventBus bus);

}
