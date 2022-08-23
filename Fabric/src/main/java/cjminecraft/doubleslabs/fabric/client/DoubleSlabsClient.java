package cjminecraft.doubleslabs.fabric.client;

import cjminecraft.doubleslabs.fabric.common.events.ConfigEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class DoubleSlabsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigEventHandler.clientRegister();
    }

}
