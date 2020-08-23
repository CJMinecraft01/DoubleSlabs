package cjminecraft.doubleslabs.client.proxy;

import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.config.ConfigEventsHandler;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy implements IProxy {
    @Override
    public void addListeners(IEventBus mod, IEventBus forge) {
        mod.addListener(this::clientSetup);
        mod.addListener(this::registerBlockColours);
        mod.addListener(this::bakeModels);
        mod.addListener(ConfigEventsHandler::onFileChange);
    }

    private void bakeModels(ModelBakeEvent event) {
        DoubleSlabBakedModel doubleSlabBakedModel = new DoubleSlabBakedModel();
        for (BlockState state : DSBlocks.DOUBLE_SLAB.get().getStateContainer().getValidStates()) {
            ModelResourceLocation variantResourceLocation = BlockModelShapes.getModelLocation(state);
            IBakedModel existingModel = event.getModelRegistry().get(variantResourceLocation);
            if (existingModel == null) {
                DoubleSlabs.LOGGER.warn("Did not find the expected vanilla baked model(s) for the vertical slab in registry");
            } else if (existingModel instanceof DoubleSlabBakedModel) {
                DoubleSlabs.LOGGER.warn("Tried to replace slab model twice");
            } else {
                event.getModelRegistry().put(variantResourceLocation, doubleSlabBakedModel);
            }
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            RenderTypeLookup.setRenderLayer(DSBlocks.DOUBLE_SLAB.get(), layer -> true);
            RenderTypeLookup.setRenderLayer(DSBlocks.VERTICAL_SLAB.get(), layer -> true);
        });
    }

    private void registerBlockColours(final ColorHandlerEvent.Block event) {
        event.getBlockColors().register(DSBlocks.DOUBLE_SLAB.get().getBlockColor(), DSBlocks.DOUBLE_SLAB.get());
        event.getBlockColors().register(DSBlocks.VERTICAL_SLAB.get().getBlockColor(), DSBlocks.VERTICAL_SLAB.get());
    }
}
