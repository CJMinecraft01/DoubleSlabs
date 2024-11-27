package cjminecraft.doubleslabs.fabric.client;

import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.fabric.client.model.MixedDoubleSlabBakedModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class DoubleSlabsClient implements ClientModInitializer {

    private static final ModelResourceLocation DOUBLE_SLABS_MODEL =
            new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "double_slab"), "");

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(this::modifyModels);
        registerBlockColours();
    }

    private void modifyModels(ModelLoadingPlugin.Context pluginContext) {
        pluginContext.modifyModelAfterBake().register((original, context) -> {
            final ModelResourceLocation location = context.topLevelId();

            if (location != null && location.equals(DOUBLE_SLABS_MODEL)) {
                return new MixedDoubleSlabBakedModel();
            } else {
                return original;
            }
        });
    }

    private void registerBlockColours() {
        ColorProviderRegistry.BLOCK.register(DynamicSlabBlockClientHooks.getBlockColour(), DSBlocks.MIXED_SLAB.get());
    }
}
