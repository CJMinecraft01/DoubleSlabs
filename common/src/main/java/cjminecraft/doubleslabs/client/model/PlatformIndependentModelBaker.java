package cjminecraft.doubleslabs.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract class PlatformIndependentModelBaker implements ModelBaker {
    protected final ModelBakery bakery;
    protected final ModelManager manager;

    public PlatformIndependentModelBaker(ModelBakery bakery, ModelManager manager) {
        this.bakery = bakery;
        this.manager = manager;
    }

    @Override
    public UnbakedModel getModel(ResourceLocation resourceLocation) {
        return bakery.getModel(resourceLocation);
    }

    @Override
    public @Nullable BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
        final var model = getModel(resourceLocation);

        return model.bake(this, this::getTextureSprite, modelState, resourceLocation);
    }

    public TextureAtlasSprite getTextureSprite(Material material) {
        return manager.getAtlas(material.atlasLocation()).getSprite(material.texture());
    }
}
