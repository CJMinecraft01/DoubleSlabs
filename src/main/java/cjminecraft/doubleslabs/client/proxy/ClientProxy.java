package cjminecraft.doubleslabs.client.proxy;

import cjminecraft.doubleslabs.client.gui.WrappedScreen;
import cjminecraft.doubleslabs.client.model.*;
import cjminecraft.doubleslabs.client.render.RaisedCampfireTileEntityRenderer;
import cjminecraft.doubleslabs.client.render.SlabTileEntityRenderer;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.vertex.VerticalSlabTransformer;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.config.ConfigEventsHandler;
import cjminecraft.doubleslabs.common.init.*;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.function.BiConsumer;

public class ClientProxy implements IProxy {
    @Override
    public void addListeners(IEventBus mod, IEventBus forge) {
        mod.addListener(this::clientSetup);
        mod.addListener(this::registerBlockColours);
        mod.addListener(this::bakeModels);
        mod.addListener(ConfigEventsHandler::onFileChange);
    }

    private void replaceModel(IBakedModel model, Block block, BiConsumer<IBakedModel, BlockState> perModel, Map<ResourceLocation, IBakedModel> registry) {
        for (BlockState state : block.getStateContainer().getValidStates()) {
            ModelResourceLocation variantResourceLocation = BlockModelShapes.getModelLocation(state);
            IBakedModel existingModel = registry.get(variantResourceLocation);
            if (existingModel == null) {
                DoubleSlabs.LOGGER.warn("Did not find the expected vanilla baked model(s) for the block in registry");
            } else if (existingModel instanceof DynamicSlabBakedModel) {
                DoubleSlabs.LOGGER.warn("Tried to replace model twice");
            } else {
                perModel.accept(existingModel, state);
                registry.put(variantResourceLocation, model);
            }
        }
    }

    private void bakeModels(ModelBakeEvent event) {
        VerticalSlabTransformer.reload();

        replaceModel(new DoubleSlabBakedModel(), DSBlocks.DOUBLE_SLAB.get(), (model, state) -> {}, event.getModelRegistry());
//        VerticalSlabBakedModel verticalSlabBakedModel = new VerticalSlabBakedModel();
        replaceModel(VerticalSlabBakedModel.INSTANCE, DSBlocks.VERTICAL_SLAB.get(), VerticalSlabBakedModel.INSTANCE::addModel, event.getModelRegistry());

        RaisedCampfireBakedModel campfireBakedModel = new RaisedCampfireBakedModel();
        replaceModel(campfireBakedModel, DSBlocks.RAISED_CAMPFIRE.get(), campfireBakedModel::addModel, event.getModelRegistry());

        ModelResourceLocation verticalSlabItemResourceLocation = new ModelResourceLocation(DSItems.VERTICAL_SLAB.getId(), "inventory");
        VerticalSlabItemBakedModel.INSTANCE = new VerticalSlabItemBakedModel(event.getModelRegistry().get(verticalSlabItemResourceLocation));
        event.getModelRegistry().put(verticalSlabItemResourceLocation, VerticalSlabItemBakedModel.INSTANCE);

//        DoubleSlabBakedModel doubleSlabBakedModel = new DoubleSlabBakedModel();
//        for (BlockState state : DSBlocks.DOUBLE_SLAB.get().getStateContainer().getValidStates()) {
//            ModelResourceLocation variantResourceLocation = BlockModelShapes.getModelLocation(state);
//            IBakedModel existingModel = event.getModelRegistry().get(variantResourceLocation);
//            if (existingModel == null) {
//                DoubleSlabs.LOGGER.warn("Did not find the expected vanilla baked model(s) for the vertical slab in registry");
//            } else if (existingModel instanceof DoubleSlabBakedModel) {
//                DoubleSlabs.LOGGER.warn("Tried to replace slab model twice");
//            } else {
//                event.getModelRegistry().put(variantResourceLocation, doubleSlabBakedModel);
//            }
//        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DSKeyBindings.register();
        RenderTypeLookup.setRenderLayer(DSBlocks.DOUBLE_SLAB.get(), layer -> true);
        RenderTypeLookup.setRenderLayer(DSBlocks.VERTICAL_SLAB.get(), layer -> true);
        RenderTypeLookup.setRenderLayer(DSBlocks.RAISED_CAMPFIRE.get(), RenderType.getCutout());
        ClientRegistry.bindTileEntityRenderer(DSTiles.DYNAMIC_SLAB.get(), SlabTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(DSTiles.CAMPFIRE.get(), RaisedCampfireTileEntityRenderer::new);
        ScreenManager.registerFactory(DSContainers.WRAPPED_CONTAINER.get(), WrappedScreen::new);

        ClientUtils.checkOptiFineInstalled();
    }

    private void registerBlockColours(final ColorHandlerEvent.Block event) {
        event.getBlockColors().register(DSBlocks.DOUBLE_SLAB.get().getBlockColor(), DSBlocks.DOUBLE_SLAB.get());
        event.getBlockColors().register(DSBlocks.VERTICAL_SLAB.get().getBlockColor(), DSBlocks.VERTICAL_SLAB.get());
    }
}
