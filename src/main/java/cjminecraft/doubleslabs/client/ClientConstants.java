package cjminecraft.doubleslabs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;

public class ClientConstants {

    private static IBakedModel fallbackModel;

    public static IBakedModel getFallbackModel() {
        if (fallbackModel != null)
            return fallbackModel;
        fallbackModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        return fallbackModel;
    }

    public static final int TINT_OFFSET = 1000;

}
