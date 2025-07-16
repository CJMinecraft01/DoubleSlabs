package cjminecraft.doubleslabs.neoforge.client;

import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.client.hooks.VerticalSlabItemClientHooks;
import cjminecraft.doubleslabs.client.model.VerticalSlabItemBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabModelBaker;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.neoforge.client.block.MixedDoubleSlabClientBlockExtensions;
import cjminecraft.doubleslabs.neoforge.client.model.MixedDoubleSlabBakedModel;
import cjminecraft.doubleslabs.neoforge.client.model.NeoForgeModelBaker;
import cjminecraft.doubleslabs.neoforge.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeBlocks;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import static cjminecraft.doubleslabs.common.init.DSBlocks.*;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class DoubleSlabsClient {

    private static final ModelResourceLocation MIXED_SLAB_MODEL = new ModelResourceLocation(MIXED_SLAB_ID, "");
    private static final ModelResourceLocation TRANSPARENT_MIXED_SLAB_MODEL = new ModelResourceLocation(TRANSPARENT_MIXED_SLAB_ID, "");
    private static final ModelResourceLocation VERTICAL_SLAB_ITEM_MODEL = new ModelResourceLocation(VERTICAL_SLAB_ID, "inventory");

    public DoubleSlabsClient(IEventBus modBus) {
        modBus.addListener(this::replaceModels);
        modBus.addListener(this::registerClientExtensions);
        modBus.addListener(this::registerBlockColours);
        modBus.addListener(this::registerItemColours);
        modBus.addListener(this::bakeModels);
    }

    private void replaceModels(final ModelEvent.ModifyBakingResult event) {
        event.getModels().put(MIXED_SLAB_MODEL, new MixedDoubleSlabBakedModel());
        event.getModels().put(TRANSPARENT_MIXED_SLAB_MODEL, new MixedDoubleSlabBakedModel());

        final var neoforgeModelBaker = new NeoForgeModelBaker(event.getModelBakery(), Minecraft.getInstance().getModelManager());
        event.getModels().put(VERTICAL_SLAB_ITEM_MODEL, new VerticalSlabItemBakedModel(neoforgeModelBaker));

        final var verticalSlabModel = new VerticalSlabBakedModel();
        VERTICAL_SLAB.get().getStateDefinition().getPossibleStates().forEach(state -> {
            final var location = BlockModelShaper.stateToModelLocation(state);
            event.getModels().put(location, verticalSlabModel);
        });
    }

    private void bakeModels(final ModelEvent.BakingCompleted event) {
        final var neoforgeModelBaker = new NeoForgeModelBaker(event.getModelBakery(), event.getModelManager());
        final var verticalSlabModelBaker = new VerticalSlabModelBaker(neoforgeModelBaker);

        verticalSlabModelBaker.bakeBlocks(BuiltInRegistries.BLOCK);
        verticalSlabModelBaker.bakeItems(BuiltInRegistries.ITEM, BuiltInRegistries.ITEM::getKey);

        ClientInternal.initialise(verticalSlabModelBaker.createModelHelper());
    }

    private void registerClientExtensions(final RegisterClientExtensionsEvent event) {
        event.registerBlock(MixedDoubleSlabClientBlockExtensions.INSTANCE, DSNeoForgeBlocks.MIXED_SLAB);
        event.registerBlock(MixedDoubleSlabClientBlockExtensions.INSTANCE, DSNeoForgeBlocks.TRANSPARENT_MIXED_SLAB);
    }

    private void registerBlockColours(final RegisterColorHandlersEvent.Block event) {
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSNeoForgeBlocks.MIXED_SLAB.get());
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSNeoForgeBlocks.TRANSPARENT_MIXED_SLAB.get());
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSNeoForgeBlocks.VERTICAL_SLAB.get());
    }

    private void registerItemColours(final RegisterColorHandlersEvent.Item event) {
        event.register(VerticalSlabItemClientHooks.getItemColour(), DSNeoForgeItems.VERTICAL_SLAB.get());
    }

}
