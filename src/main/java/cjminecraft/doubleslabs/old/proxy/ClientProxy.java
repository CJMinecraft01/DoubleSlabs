package cjminecraft.doubleslabs.old.proxy;

import cjminecraft.doubleslabs.old.DoubleSlabs;
import cjminecraft.doubleslabs.old.Registrar;
import cjminecraft.doubleslabs.old.Utils;
import cjminecraft.doubleslabs.old.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.old.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.old.client.render.TileEntityRendererDoubleSlab;
import cjminecraft.doubleslabs.old.client.render.TileEntityRendererVerticalSlab;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy implements IProxy {
    @Override
    public void addListeners(IEventBus bus) {
        bus.addListener(ClientProxy::bakeModels);
        bus.addListener(ClientProxy::registerBlockColours);
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(Registrar.DOUBLE_SLAB, layer -> true);
        RenderTypeLookup.setRenderLayer(Registrar.VERTICAL_SLAB, layer -> true);
        Utils.checkOptiFineInstalled();
        ClientRegistry.bindTileEntityRenderer(Registrar.TILE_VERTICAL_SLAB, TileEntityRendererVerticalSlab::new);
        ClientRegistry.bindTileEntityRenderer(Registrar.TILE_DOUBLE_SLAB, TileEntityRendererDoubleSlab::new);
    }

    private static void bakeModels(ModelBakeEvent event) {
        event.getModelRegistry().put(new ModelResourceLocation(new ResourceLocation(DoubleSlabs.MODID, "double_slab"), ""), new DoubleSlabBakedModel());

        VerticalSlabBakedModel verticalSlabBakedModel = new VerticalSlabBakedModel();

        for (BlockState state : Registrar.VERTICAL_SLAB.getStateContainer().getValidStates()) {
            ModelResourceLocation variantResourceLocation = BlockModelShapes.getModelLocation(state);
            IBakedModel existingModel = event.getModelRegistry().get(variantResourceLocation);
            if (existingModel == null) {
                DoubleSlabs.LOGGER.warn("Did not find the expected vanilla baked model(s) for the vertical slab in registry");
            } else if (existingModel instanceof VerticalSlabBakedModel) {
                DoubleSlabs.LOGGER.warn("Tried to replace VerticalSlabBakedModel twice");
            } else {
                verticalSlabBakedModel.addModel(existingModel, state);
                event.getModelRegistry().put(variantResourceLocation, verticalSlabBakedModel);
            }
        }
    }

    private static void registerBlockColours(ColorHandlerEvent.Block event) {
        event.getBlockColors().register(Registrar.DOUBLE_SLAB.getBlockColor(), Registrar.DOUBLE_SLAB);
        event.getBlockColors().register(Registrar.VERTICAL_SLAB.getBlockColor(), Registrar.VERTICAL_SLAB);
    }
}
