package cjminecraft.doubleslabs.forge.client;

import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.forge.client.model.MixedDoubleSlabBakedModel;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlocks;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import static cjminecraft.doubleslabs.common.init.DSBlocks.MIXED_SLAB_ID;
import static cjminecraft.doubleslabs.common.init.DSBlocks.TRANSPARENT_MIXED_SLAB_ID;

public class DoubleSlabsClient {

    private static final ModelResourceLocation MIXED_SLAB_MODEL = new ModelResourceLocation(MIXED_SLAB_ID, "");
    private static final ModelResourceLocation TRANSPARENT_MIXED_SLAB_MODEL = new ModelResourceLocation(TRANSPARENT_MIXED_SLAB_ID, "");

    public static void addListeners(IEventBus mod) {
        mod.addListener(DoubleSlabsClient::bakeModels);
        mod.addListener(DoubleSlabsClient::registerBlockColours);
    }

    private static void bakeModels(final ModelEvent.ModifyBakingResult event) {
        event.getModels().put(MIXED_SLAB_MODEL, new MixedDoubleSlabBakedModel());
        event.getModels().put(TRANSPARENT_MIXED_SLAB_MODEL, new MixedDoubleSlabBakedModel());
    }

    private static void registerBlockColours(final RegisterColorHandlersEvent.Block event) {
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSForgeBlocks.MIXED_SLAB.get());
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSForgeBlocks.TRANSPARENT_MIXED_SLAB.get());
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSForgeBlocks.VERTICAL_SLAB.get());
    }

}
