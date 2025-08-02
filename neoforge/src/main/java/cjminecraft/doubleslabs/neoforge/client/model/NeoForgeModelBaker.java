package cjminecraft.doubleslabs.neoforge.client.model;

import cjminecraft.doubleslabs.client.model.PlatformIndependentModelBaker;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class NeoForgeModelBaker extends PlatformIndependentModelBaker {
    public NeoForgeModelBaker(ModelBakery bakery, ModelManager manager) {
        super(bakery, manager);
    }

    @Override
    public @Nullable UnbakedModel getTopLevelModel(ModelResourceLocation modelResourceLocation) {
        return null;
    }

    @Override
    public @Nullable BakedModel bake(ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> sprites) {
        final var model = getModel(resourceLocation);

        return model.bake(this, sprites, modelState);
    }

    @Override
    public @Nullable BakedModel bakeUncached(UnbakedModel unbakedModel, ModelState modelState, Function<Material, TextureAtlasSprite> sprites) {
        return unbakedModel.bake(this, sprites, modelState);
    }

    @Override
    public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
        return this::getTextureSprite;
    }
}
