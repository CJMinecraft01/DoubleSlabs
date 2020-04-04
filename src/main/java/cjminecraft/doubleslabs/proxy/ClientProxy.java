package cjminecraft.doubleslabs.proxy;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientProxy implements IProxy {
    @Override
    public void setup(IEventBus bus) {
        bus.addListener(ClientProxy::bakeModels);
        bus.addListener(ClientProxy::registerBlockColours);
    }

    private static void bakeModels(ModelBakeEvent event) {
        event.getModelRegistry().put(new ModelResourceLocation(new ResourceLocation(DoubleSlabs.MODID, "double_slab"), ""), new DoubleSlabBakedModel());
    }

    private static void registerBlockColours(ColorHandlerEvent.Block event) {
        event.getBlockColors().register(Registrar.DOUBLE_SLAB.getBlockColor(), Registrar.DOUBLE_SLAB);
    }
}
