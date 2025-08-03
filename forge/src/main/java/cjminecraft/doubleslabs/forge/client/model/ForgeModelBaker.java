package cjminecraft.doubleslabs.forge.client.model;

import cjminecraft.doubleslabs.client.model.PlatformIndependentModelBaker;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ForgeModelBaker extends PlatformIndependentModelBaker {

    public ForgeModelBaker(ModelBakery bakery, ModelManager manager) {
        super(bakery, manager);
    }

    @Override
    public @Nullable BakedModel bake(ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> function) {
        final var model = getModel(resourceLocation);

        return model.bake(this, function, modelState, resourceLocation);
    }

    @Override
    public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
        return this::getTextureSprite;
    }
}
