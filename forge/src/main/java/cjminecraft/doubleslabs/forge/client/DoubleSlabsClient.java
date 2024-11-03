package cjminecraft.doubleslabs.forge.client;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.forge.client.model.MixedDoubleSlabBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class DoubleSlabsClient {

    private static final ModelResourceLocation DOUBLE_SLABS_MODEL =
            new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "double_slab"), "");

    public static void addListeners(IEventBus mod) {
        mod.addListener(DoubleSlabsClient::bakeModels);
    }

    private static void bakeModels(final ModelEvent.ModifyBakingResult event) {
        event.getModels().put(DOUBLE_SLABS_MODEL, new MixedDoubleSlabBakedModel());
    }

}
