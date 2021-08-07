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
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.eventbus.api.IEventBus;
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

    private void replaceModel(BakedModel model, Block block, BiConsumer<BakedModel, BlockState> perModel, Map<ResourceLocation, BakedModel> registry) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            ModelResourceLocation variantResourceLocation = BlockModelShaper.stateToModelLocation(state);
            BakedModel existingModel = registry.get(variantResourceLocation);
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

    public static final Transformation RAISED_CAMPFIRE_TRANSFORM = new Transformation(new Vector3f(0, 0.5f, 0), null, null, null);

    private void replaceCampfireModel(Block block, Map<ResourceLocation, BakedModel> registry, ModelLoader loader) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            ModelResourceLocation variantResourceLocation = BlockModelShaper.stateToModelLocation(state);
            UnbakedModel existingModel = loader.getModel(variantResourceLocation);
            registry.put(variantResourceLocation, ClientConstants.bake(loader, existingModel, variantResourceLocation, false, new SimpleModelState(RAISED_CAMPFIRE_TRANSFORM)));
        }
    }

    private void bakeModels(ModelBakeEvent event) {
        ClientConstants.bakeVerticalSlabModels(event.getModelLoader());

        replaceModel(new DoubleSlabBakedModel(), DSBlocks.DOUBLE_SLAB.get(), (model, state) -> {}, event.getModelRegistry());
        replaceModel(VerticalSlabBakedModel.INSTANCE, DSBlocks.VERTICAL_SLAB.get(), VerticalSlabBakedModel.INSTANCE::addModel, event.getModelRegistry());

        replaceCampfireModel(DSBlocks.RAISED_CAMPFIRE.get(), event.getModelRegistry(), event.getModelLoader());
        replaceCampfireModel(DSBlocks.RAISED_SOUL_CAMPFIRE.get(), event.getModelRegistry(), event.getModelLoader());
//        ModList list = ModList.get();
//        if (list.isLoaded("endergetic")) {
//            replaceCampfireModel(DSBlocks.RAISED_ENDER_CAMPFIRE.get(), event.getModelRegistry(), event.getModelLoader());
//        }
//        if (list.isLoaded("byg")) {
//            replaceCampfireModel(DSBlocks.RAISED_BORIC_CAMPFIRE.get(), event.getModelRegistry(), event.getModelLoader());
//            replaceCampfireModel(DSBlocks.RAISED_CRYPTIC_CAMPFIRE.get(), event.getModelRegistry(), event.getModelLoader());
//        }

        ModelResourceLocation verticalSlabItemResourceLocation = new ModelResourceLocation(DSItems.VERTICAL_SLAB.getId(), "inventory");
        VerticalSlabItemBakedModel.INSTANCE = new VerticalSlabItemBakedModel(event.getModelRegistry().get(verticalSlabItemResourceLocation));
        event.getModelRegistry().put(verticalSlabItemResourceLocation, VerticalSlabItemBakedModel.INSTANCE);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DSKeyBindings.register();
        ItemBlockRenderTypes.setRenderLayer(DSBlocks.DOUBLE_SLAB.get(), layer -> true);
        ItemBlockRenderTypes.setRenderLayer(DSBlocks.VERTICAL_SLAB.get(), layer -> true);
        ItemBlockRenderTypes.setRenderLayer(DSBlocks.RAISED_CAMPFIRE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(DSBlocks.RAISED_SOUL_CAMPFIRE.get(), RenderType.cutout());
//        ModList list = ModList.get();
//        if (list.isLoaded("endergetic")) {
//            ItemBlockRenderTypes.setRenderLayer(DSBlocks.RAISED_ENDER_CAMPFIRE.get(), RenderType.getCutout());
//        }
//        if (list.isLoaded("byg")) {
//            ItemBlockRenderTypes.setRenderLayer(DSBlocks.RAISED_BORIC_CAMPFIRE.get(), RenderType.getCutout());
//            ItemBlockRenderTypes.setRenderLayer(DSBlocks.RAISED_CRYPTIC_CAMPFIRE.get(), RenderType.getCutout());
//        }
        BlockEntityRenderers.register(DSTiles.DYNAMIC_SLAB.get(), SlabTileEntityRenderer::new);
        BlockEntityRenderers.register(DSTiles.CAMPFIRE.get(), RaisedCampfireTileEntityRenderer::new);
        MenuScreens.register(DSContainers.WRAPPED_CONTAINER.get(), WrappedScreen::new);
    }

    private void registerBlockColours(final ColorHandlerEvent.Block event) {
        event.getBlockColors().register(DSBlocks.DOUBLE_SLAB.get().getBlockColor(), DSBlocks.DOUBLE_SLAB.get());
        event.getBlockColors().register(DSBlocks.VERTICAL_SLAB.get().getBlockColor(), DSBlocks.VERTICAL_SLAB.get());
    }
}
