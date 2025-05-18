package cjminecraft.doubleslabs.fabric.client;

import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.fabric.client.model.MixedDoubleSlabBakedModel;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;

import static cjminecraft.doubleslabs.common.init.DSBlocks.MIXED_SLAB_ID;
import static cjminecraft.doubleslabs.common.init.DSBlocks.TRANSPARENT_MIXED_SLAB_ID;

public class DoubleSlabsClient implements ClientModInitializer {

    private static final ModelResourceLocation MIXED_SLAB_MODEL = new ModelResourceLocation(MIXED_SLAB_ID, "");
    private static final ModelResourceLocation TRANSPARENT_MIXED_SLAB_MODEL = new ModelResourceLocation(TRANSPARENT_MIXED_SLAB_ID, "");

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(this::modifyModels);
        registerBlockColours();
    }

    private void modifyModels(ModelLoadingPlugin.Context pluginContext) {
        pluginContext.modifyModelAfterBake().register((original, context) -> {
            final var location = context.topLevelId();

            if (location != null && (location.equals(MIXED_SLAB_MODEL) || location.equals(TRANSPARENT_MIXED_SLAB_MODEL))) {
                return new MixedDoubleSlabBakedModel();
            } else {
                return original;
            }
        });
    }

    private void registerBlockColours() {
        ColorProviderRegistry.BLOCK.register(DynamicSlabBlockClientHooks.getBlockColour(), DSFabricBlocks.MIXED_SLAB);
        ColorProviderRegistry.BLOCK.register(DynamicSlabBlockClientHooks.getBlockColour(), DSFabricBlocks.TRANSPARENT_MIXED_SLAB);
    }
}
