package cjminecraft.doubleslabs.forge.client;

import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.forge.client.model.MixedDoubleSlabBakedModel;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlocks;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class DoubleSlabsClient {

    private static final ModelResourceLocation DOUBLE_SLABS_MODEL =
            new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "double_slab"), "");

    public static void addListeners(IEventBus mod) {
        mod.addListener(DoubleSlabsClient::bakeModels);
        mod.addListener(DoubleSlabsClient::registerBlockColours);
    }

    private static void bakeModels(final ModelEvent.ModifyBakingResult event) {
        event.getModels().put(DOUBLE_SLABS_MODEL, new MixedDoubleSlabBakedModel());
    }

    private static void registerBlockColours(final RegisterColorHandlersEvent.Block event) {
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSForgeBlocks.MIXED_SLAB.get());
    }

}
