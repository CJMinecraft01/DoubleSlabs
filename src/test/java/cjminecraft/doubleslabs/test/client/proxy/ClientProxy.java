package cjminecraft.doubleslabs.test.client.proxy;

import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.test.common.init.DSTBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy implements IProxy {
    @Override
    public void addListeners(IEventBus mod, IEventBus forge) {
        mod.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            RenderTypeLookup.setRenderLayer(DSTBlocks.GLASS_SLAB.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(DSTBlocks.SLIME_SLAB.get(), RenderType.getTranslucent());
        });
    }
}
