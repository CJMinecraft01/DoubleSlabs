package cjminecraft.doubleslabs.test.client.proxy;

import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.test.client.gui.ChestSlabScreen;
import cjminecraft.doubleslabs.test.common.init.DSTBlocks;
import cjminecraft.doubleslabs.test.common.init.DSTContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy implements IProxy {
    @Override
    public void addListeners(IEventBus mod, IEventBus forge) {
        mod.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(DSTBlocks.GLASS_SLAB.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(DSTBlocks.SLIME_SLAB.get(), RenderType.translucent());
        MenuScreens.register(DSTContainers.CHEST_SLAB.get(), ChestSlabScreen::new);
    }
}
