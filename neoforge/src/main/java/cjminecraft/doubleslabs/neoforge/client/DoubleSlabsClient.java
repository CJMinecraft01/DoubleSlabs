package cjminecraft.doubleslabs.neoforge.client;

import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.neoforge.client.block.MixedDoubleSlabClientBlockExtensions;
import cjminecraft.doubleslabs.neoforge.client.model.MixedDoubleSlabBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class DoubleSlabsClient {

    private static final ModelResourceLocation DOUBLE_SLABS_MODEL =
            new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "double_slab"), "");

    public DoubleSlabsClient(IEventBus modBus) {
        modBus.addListener(this::bakeModels);
        modBus.addListener(this::registerClientExtensions);
        modBus.addListener(this::registerBlockColours);
    }

    private void bakeModels(final ModelEvent.ModifyBakingResult event) {
        event.getModels().put(DOUBLE_SLABS_MODEL, new MixedDoubleSlabBakedModel());
    }

    private void registerClientExtensions(final RegisterClientExtensionsEvent event) {
        event.registerBlock(MixedDoubleSlabClientBlockExtensions.INSTANCE, DSBlocks.MIXED_SLAB);
    }

    private void registerBlockColours(final RegisterColorHandlersEvent.Block event) {
        event.register(DynamicSlabBlockClientHooks.getBlockColour(), DSBlocks.MIXED_SLAB);
    }

}
