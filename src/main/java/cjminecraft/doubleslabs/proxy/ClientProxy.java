package cjminecraft.doubleslabs.proxy;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
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

        VerticalSlabBakedModel verticalSlabBakedModel = new VerticalSlabBakedModel();

        for (BlockState state : Registrar.VERTICAL_SLAB.getStateContainer().getValidStates()) {
            ModelResourceLocation variantResourceLocation = BlockModelShapes.getModelLocation(state);
            IBakedModel existingModel = event.getModelRegistry().get(variantResourceLocation);
            if (existingModel == null) {
                DoubleSlabs.LOGGER.warn("Did not find the expected vanilla baked model(s) for the vertical slab in registry");
            } else if (existingModel instanceof VerticalSlabBakedModel) {
                DoubleSlabs.LOGGER.warn("Tried to replace VerticalSlabBakedModel twice");
            } else {
                event.getModelRegistry().put(variantResourceLocation, verticalSlabBakedModel);
            }
        }
    }

    private static void registerBlockColours(ColorHandlerEvent.Block event) {
        event.getBlockColors().register(Registrar.DOUBLE_SLAB.getBlockColor(), Registrar.DOUBLE_SLAB);
        event.getBlockColors().register(Registrar.VERTICAL_SLAB.getBlockColor(), Registrar.VERTICAL_SLAB);
    }
}
