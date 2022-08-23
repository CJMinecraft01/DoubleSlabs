package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.platform.services.IClientHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;

@Environment(EnvType.CLIENT)
public class FabricClientHelper implements IClientHelper {
    @Override
    public AtlasSet getAtlasSet(ModelBakery bakery) {
        return bakery.atlasSet;
    }
}
