package cjminecraft.doubleslabs.fabric.client;

import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.client.hooks.VerticalSlabItemClientHooks;
import cjminecraft.doubleslabs.client.model.VerticalSlabItemBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabModelBaker;
import cjminecraft.doubleslabs.fabric.client.model.FabricModelBaker;
import cjminecraft.doubleslabs.fabric.client.model.MixedDoubleSlabBakedModel;
import cjminecraft.doubleslabs.fabric.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import static cjminecraft.doubleslabs.common.init.DSBlocks.*;

public class DoubleSlabsClient implements ClientModInitializer {

    private static final ModelResourceLocation MIXED_SLAB_MODEL = new ModelResourceLocation(MIXED_SLAB_ID, "");
    private static final ModelResourceLocation TRANSPARENT_MIXED_SLAB_MODEL = new ModelResourceLocation(TRANSPARENT_MIXED_SLAB_ID, "");
    private static final ModelResourceLocation VERTICAL_SLAB_ITEM_MODEL = new ModelResourceLocation(VERTICAL_SLAB_ID, "inventory");

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(this::modifyModels);
        registerItemColours();
    }

    private void modifyModels(ModelLoadingPlugin.Context pluginContext) {
        pluginContext.modifyModelAfterBake().register((original, context) -> {
            final var location = context.topLevelId();

            if (location == null) {
                return original;
            }

            if (location.equals(MIXED_SLAB_MODEL) || location.equals(TRANSPARENT_MIXED_SLAB_MODEL)) {
                return new MixedDoubleSlabBakedModel();
            }

            if (location.equals(VERTICAL_SLAB_ITEM_MODEL)) {
                return new VerticalSlabItemBakedModel(context.baker());
            }

            if (location.id().equals(VERTICAL_SLAB_ID)) {
                return new VerticalSlabBakedModel();
            }

            return original;
        });
    }

    public static void bakeVerticalSlabs(ModelBakery modelBakery, ModelManager modelManager) {
        final var verticalSlabModelBaker = new VerticalSlabModelBaker(new FabricModelBaker(modelBakery, modelManager));

        verticalSlabModelBaker.bakeBlocks(BuiltInRegistries.BLOCK);
        verticalSlabModelBaker.bakeItems(BuiltInRegistries.ITEM, BuiltInRegistries.ITEM::getKey);

        ClientInternal.initialise(verticalSlabModelBaker.createModelHelper());
    }

    private static void registerItemColours() {
        ColorProviderRegistry.ITEM.register(VerticalSlabItemClientHooks.getItemColour(), DSFabricItems.VERTICAL_SLAB);
    }

}
