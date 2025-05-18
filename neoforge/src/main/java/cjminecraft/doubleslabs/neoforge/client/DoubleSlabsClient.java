package cjminecraft.doubleslabs.neoforge.client;

import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.neoforge.client.block.MixedDoubleSlabClientBlockExtensions;
import cjminecraft.doubleslabs.neoforge.client.model.MixedDoubleSlabBakedModel;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeBlocks;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import static cjminecraft.doubleslabs.common.init.DSBlocks.MIXED_SLAB_ID;
import static cjminecraft.doubleslabs.common.init.DSBlocks.TRANSPARENT_MIXED_SLAB_ID;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class DoubleSlabsClient {

    private static final ModelResourceLocation MIXED_SLAB_MODEL = new ModelResourceLocation(MIXED_SLAB_ID, "");
    private static final ModelResourceLocation TRANSPARENT_MIXED_SLAB_MODEL = new ModelResourceLocation(TRANSPARENT_MIXED_SLAB_ID, "");

    public DoubleSlabsClient(IEventBus modBus) {
        modBus.addListener(this::bakeModels);
        modBus.addListener(this::registerClientExtensions);
        modBus.addListener(this::registerBlockColours);
    }

    private void bakeModels(final ModelEvent.ModifyBakingResult event) {
        event.getModels().put(MIXED_SLAB_MODEL, new MixedDoubleSlabBakedModel());
        event.getModels().put(TRANSPARENT_MIXED_SLAB_MODEL, new MixedDoubleSlabBakedModel());
    }

    private void registerClientExtensions(final RegisterClientExtensionsEvent event) {
        event.registerBlock(MixedDoubleSlabClientBlockExtensions.INSTANCE, DSNeoForgeBlocks.MIXED_SLAB);
        event.registerBlock(MixedDoubleSlabClientBlockExtensions.INSTANCE, DSNeoForgeBlocks.TRANSPARENT_MIXED_SLAB);
    }

    private void registerBlockColours(final RegisterColorHandlersEvent.Block event) {
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSNeoForgeBlocks.MIXED_SLAB.get());
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSNeoForgeBlocks.TRANSPARENT_MIXED_SLAB.get());
    }

}
