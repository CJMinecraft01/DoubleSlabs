package cjminecraft.doubleslabs.platform.services;

import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;

public interface IClientHelper {

    AtlasSet getAtlasSet(ModelBakery bakery);

}
