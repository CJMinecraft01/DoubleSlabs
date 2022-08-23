package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.platform.services.IClientHelper;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ForgeClientHelper implements IClientHelper {
    @Override
    public AtlasSet getAtlasSet(ModelBakery bakery) {
        return bakery.getAtlasSet();
    }
}
