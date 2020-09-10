package cjminecraft.doubleslabs.test.client.proxy;

import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.test.client.gui.ChestSlabScreen;
import cjminecraft.doubleslabs.test.common.init.DSTContainers;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy implements IProxy {
    @Override
    public void addListeners(IEventBus mod, IEventBus forge) {
        mod.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(DSTContainers.CHEST_SLAB.get(), ChestSlabScreen::new);
    }
}
