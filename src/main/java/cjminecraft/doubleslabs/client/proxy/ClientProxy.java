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
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.init.DSContainers;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.init.DSKeyBindings;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import javax.vecmath.Vector3f;
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

    public static final TRSRTransformation RAISED_CAMPFIRE_TRANSFORM = new TRSRTransformation(new Vector3f(0, 0.5f, 0), null, null, null);

    private void replaceCampfireModel(Block block, Map<ResourceLocation, IBakedModel> registry, ModelLoader loader) {
        for (BlockState state : block.getStateContainer().getValidStates()) {
            ModelResourceLocation variantResourceLocation = BlockModelShapes.getModelLocation(state);
            IUnbakedModel existingModel = loader.getUnbakedModel(variantResourceLocation);
            registry.put(variantResourceLocation, ClientConstants.bake(loader, existingModel, variantResourceLocation, false, new BasicState(RAISED_CAMPFIRE_TRANSFORM, false)));
        }
    }

    private void bakeModels(ModelBakeEvent event) {
        ClientConstants.bakeVerticalSlabModels(event.getModelLoader());

        replaceModel(new DoubleSlabBakedModel(), DSBlocks.DOUBLE_SLAB.get(), (model, state) -> {}, event.getModelRegistry());
        replaceModel(VerticalSlabBakedModel.INSTANCE, DSBlocks.VERTICAL_SLAB.get(), VerticalSlabBakedModel.INSTANCE::addModel, event.getModelRegistry());

        replaceCampfireModel(DSBlocks.RAISED_CAMPFIRE.get(), event.getModelRegistry(), event.getModelLoader());

        ModelResourceLocation verticalSlabItemResourceLocation = new ModelResourceLocation(DSItems.VERTICAL_SLAB.getId(), "inventory");
        VerticalSlabItemBakedModel.INSTANCE = new VerticalSlabItemBakedModel(event.getModelRegistry().get(verticalSlabItemResourceLocation));
        event.getModelRegistry().put(verticalSlabItemResourceLocation, VerticalSlabItemBakedModel.INSTANCE);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DSKeyBindings.register();
        ClientRegistry.bindTileEntitySpecialRenderer(SlabTileEntity.class, new SlabTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(RaisedCampfireTileEntity.class, new RaisedCampfireTileEntityRenderer());
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
