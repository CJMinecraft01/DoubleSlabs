package cjminecraft.doubleslabs.client.proxy;

import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.gui.WrappedScreen;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabItemBakedModel;
import cjminecraft.doubleslabs.client.render.RaisedCampfireTileEntityRenderer;
import cjminecraft.doubleslabs.client.render.SlabTileEntityRenderer;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.config.ConfigEventsHandler;
import cjminecraft.doubleslabs.common.init.*;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.Map;
import java.util.function.BiConsumer;

public class ClientProxy implements IProxy {
    @Override
    public void addListeners(IEventBus mod, IEventBus forge) {
        mod.addListener(this::clientSetup);
//        mod.addListener(this::registerBlockColours);
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

    public static final TransformationMatrix RAISED_CAMPFIRE_TRANSFORM = new TransformationMatrix(new Vector3f(0, 0.5f, 0), null, null, null);

    private void replaceCampfireModel(Block block, Map<ResourceLocation, IBakedModel> registry, ModelLoader loader) {
        for (BlockState state : block.getStateContainer().getValidStates()) {
            ModelResourceLocation variantResourceLocation = BlockModelShapes.getModelLocation(state);
            IUnbakedModel existingModel = loader.getUnbakedModel(variantResourceLocation);
            registry.put(variantResourceLocation, ClientConstants.bake(loader, existingModel, variantResourceLocation, false, new SimpleModelTransform(RAISED_CAMPFIRE_TRANSFORM)));
        }
    }

    private void bakeModels(ModelBakeEvent event) {
        ClientConstants.bakeVerticalSlabModels(event.getModelLoader());

        replaceModel(new DoubleSlabBakedModel(), DSBlocks.DOUBLE_SLAB.get(), (model, state) -> {}, event.getModelRegistry());
        replaceModel(VerticalSlabBakedModel.INSTANCE, DSBlocks.VERTICAL_SLAB.get(), VerticalSlabBakedModel.INSTANCE::addModel, event.getModelRegistry());

        replaceCampfireModel(DSBlocks.RAISED_CAMPFIRE.get(), event.getModelRegistry(), event.getModelLoader());

        if (ModList.get().isLoaded("extendednether"))
            replaceCampfireModel(DSBlocks.RAISED_SOUL_CAMPFIRE.get(), event.getModelRegistry(), event.getModelLoader());

        ModelResourceLocation verticalSlabItemResourceLocation = new ModelResourceLocation(DSItems.VERTICAL_SLAB.getId(), "inventory");
        VerticalSlabItemBakedModel.INSTANCE = new VerticalSlabItemBakedModel(event.getModelRegistry().get(verticalSlabItemResourceLocation));
        event.getModelRegistry().put(verticalSlabItemResourceLocation, VerticalSlabItemBakedModel.INSTANCE);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DSKeyBindings.register();
        RenderTypeLookup.setRenderLayer(DSBlocks.DOUBLE_SLAB.get(), layer -> true);
        RenderTypeLookup.setRenderLayer(DSBlocks.VERTICAL_SLAB.get(), layer -> true);
        RenderTypeLookup.setRenderLayer(DSBlocks.RAISED_CAMPFIRE.get(), RenderType.getCutout());
        if (ModList.get().isLoaded("extendednether"))
            RenderTypeLookup.setRenderLayer(DSBlocks.RAISED_SOUL_CAMPFIRE.get(), RenderType.getCutout());
        ClientRegistry.bindTileEntityRenderer(DSTiles.DYNAMIC_SLAB.get(), SlabTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(DSTiles.CAMPFIRE.get(), RaisedCampfireTileEntityRenderer::new);
        ScreenManager.registerFactory(DSContainers.WRAPPED_CONTAINER.get(), WrappedScreen::new);
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        // Register block colors on load complete since using the register event causes a crash
        registerBlockColours(Minecraft.getInstance().getBlockColors());
    }

    private void registerBlockColours(BlockColors colors) {
        colors.register(DSBlocks.DOUBLE_SLAB.get().getBlockColor(), DSBlocks.DOUBLE_SLAB.get());
        colors.register(DSBlocks.VERTICAL_SLAB.get().getBlockColor(), DSBlocks.VERTICAL_SLAB.get());
    }
}
