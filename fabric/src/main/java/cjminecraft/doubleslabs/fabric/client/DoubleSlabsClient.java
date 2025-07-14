package cjminecraft.doubleslabs.fabric.client;

import cjminecraft.doubleslabs.client.model.VerticalSlabItemBakedModel;
import cjminecraft.doubleslabs.fabric.client.model.MixedDoubleSlabBakedModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;

import static cjminecraft.doubleslabs.common.init.DSBlocks.*;

public class DoubleSlabsClient implements ClientModInitializer {

    private static final ModelResourceLocation MIXED_SLAB_MODEL = new ModelResourceLocation(MIXED_SLAB_ID, "");
    private static final ModelResourceLocation TRANSPARENT_MIXED_SLAB_MODEL = new ModelResourceLocation(TRANSPARENT_MIXED_SLAB_ID, "");
    private static final ModelResourceLocation VERTICAL_SLAB_ITEM_MODEL = new ModelResourceLocation(VERTICAL_SLAB_ID, "inventory");

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(this::modifyModels);
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

            return original;
        });
    }

}
