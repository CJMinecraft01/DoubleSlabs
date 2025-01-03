package cjminecraft.doubleslabs.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;

public abstract class DynamicSlabBakedModel implements BakedModel {

    protected static BakedModel getFallbackModel() {
        return Minecraft.getInstance().getModelManager().getMissingModel();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getFallbackModel().getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return getFallbackModel().getTransforms();
    }

    @Override
    public BakedOverrides overrides() {
        return BakedOverrides.EMPTY;
    }
}
