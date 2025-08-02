package cjminecraft.doubleslabs.client;

import cjminecraft.doubleslabs.library.helpers.VerticalSlabModelHelper;
import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;

import javax.annotation.Nullable;

public class ClientInternal {

    public static final int TINT_OFFSET = 1000;

    public static BakedModel getFallbackModel() {
        return Minecraft.getInstance().getModelManager().getMissingModel();
    }

    @Nullable
    private static VerticalSlabModelHelper verticalSlabModelHelper;

    public static VerticalSlabModelHelper getVerticalSlabModelHelper() {
        Preconditions.checkNotNull(verticalSlabModelHelper, "VerticalSlabModelHelper not initialized");
        return verticalSlabModelHelper;
    }

    public static void initialise(VerticalSlabModelHelper verticalSlabModelHelper) {
        ClientInternal.verticalSlabModelHelper = verticalSlabModelHelper;
    }

}
