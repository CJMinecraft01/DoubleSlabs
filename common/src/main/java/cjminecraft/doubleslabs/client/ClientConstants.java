package cjminecraft.doubleslabs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;

public class ClientConstants {

    public static final int TINT_OFFSET = 1000;

    public static BakedModel getFallbackModel() {
        return Minecraft.getInstance().getModelManager().getMissingModel();
    }

}
