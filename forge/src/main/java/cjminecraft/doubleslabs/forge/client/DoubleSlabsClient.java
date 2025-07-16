package cjminecraft.doubleslabs.forge.client;

import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.client.hooks.VerticalSlabItemClientHooks;
import cjminecraft.doubleslabs.client.model.VerticalSlabItemBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabModelBaker;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.forge.client.model.ForgeModelBaker;
import cjminecraft.doubleslabs.forge.client.model.MixedDoubleSlabBakedModel;
import cjminecraft.doubleslabs.forge.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;

import static cjminecraft.doubleslabs.common.init.DSBlocks.*;

public class DoubleSlabsClient {

    private static final ModelResourceLocation MIXED_SLAB_MODEL = new ModelResourceLocation(MIXED_SLAB_ID, "");
    private static final ModelResourceLocation TRANSPARENT_MIXED_SLAB_MODEL = new ModelResourceLocation(TRANSPARENT_MIXED_SLAB_ID, "");
    private static final ModelResourceLocation VERTICAL_SLAB_ITEM_MODEL = new ModelResourceLocation(VERTICAL_SLAB_ID, "inventory");

    public static void addListeners(IEventBus mod) {
        mod.addListener(DoubleSlabsClient::replaceModels);
        mod.addListener(DoubleSlabsClient::registerBlockColours);
        mod.addListener(DoubleSlabsClient::registerItemColours);
        mod.addListener(DoubleSlabsClient::bakeModels);
    }

    private static void replaceModels(final ModelEvent.ModifyBakingResult event) {
        event.getModels().put(MIXED_SLAB_MODEL, new MixedDoubleSlabBakedModel());
        event.getModels().put(TRANSPARENT_MIXED_SLAB_MODEL, new MixedDoubleSlabBakedModel());

        final var forgeModelBaker = new ForgeModelBaker(event.getModelBakery(), Minecraft.getInstance().getModelManager());
        event.getModels().put(VERTICAL_SLAB_ITEM_MODEL, new VerticalSlabItemBakedModel(forgeModelBaker));

        final var verticalSlabModel = new VerticalSlabBakedModel();
        VERTICAL_SLAB.get().getStateDefinition().getPossibleStates().forEach(state -> {
            final var location = BlockModelShaper.stateToModelLocation(state);
            event.getModels().put(location, verticalSlabModel);
        });
    }

    private static void bakeModels(final ModelEvent.BakingCompleted event) {
        final var forgeModelBaker = new ForgeModelBaker(event.getModelBakery(), event.getModelManager());
        final var verticalSlabModelBaker = new VerticalSlabModelBaker(forgeModelBaker);

        verticalSlabModelBaker.bakeBlocks(ForgeRegistries.BLOCKS);
        verticalSlabModelBaker.bakeItems(ForgeRegistries.ITEMS, ForgeRegistries.ITEMS::getKey);

        ClientInternal.initialise(verticalSlabModelBaker.createModelHelper());
    }

    private static void registerBlockColours(final RegisterColorHandlersEvent.Block event) {
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSForgeBlocks.MIXED_SLAB.get());
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSForgeBlocks.TRANSPARENT_MIXED_SLAB.get());
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSForgeBlocks.VERTICAL_SLAB.get());
    }

    private static void registerItemColours(final RegisterColorHandlersEvent.Item event) {
        event.register(VerticalSlabItemClientHooks.getItemColour(), DSItems.VERTICAL_SLAB.get());
    }

}
