package cjminecraft.doubleslabs.client.model;

import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;

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

    public @Nullable UnbakedModel getTopLevelModel(ModelResourceLocation modelResourceLocation) {
        return bakery.topLevelModels.get(modelResourceLocation);
    }

    @Override
    public @Nullable BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
        return bake(getModel(resourceLocation), resourceLocation, modelState);
    }

    public @Nullable BakedModel bake(ModelResourceLocation modelResourceLocation, ModelState modelState) {
        return bake(getTopLevelModel(modelResourceLocation), modelResourceLocation, modelState);
    }

    private @Nullable BakedModel bake(@Nullable final UnbakedModel model, final ResourceLocation resourceLocation, final ModelState modelState) {
        if (model == null) {
            return null;
        }

        // Baking a multi-variant model will not use the provided model state
        if (model instanceof MultiVariant multiVariant) {
            if (multiVariant.getVariants().isEmpty()) {
                return null;
            }

            final var builder = new WeightedBakedModel.Builder();

            for (final var variant : multiVariant.getVariants()) {
                // Construct a new variant which is a combination of the original variant and the new variant to bake
                final var bakedVariant = bake(variant.getModelLocation(), new Variant(
                        variant.getModelLocation(),
                        modelState.getRotation().compose(variant.getRotation()),
                        modelState.isUvLocked() || variant.isUvLocked(), variant.getWeight())
                );
                builder.add(bakedVariant, variant.getWeight());
            }

            return builder.build();
        }

        return model.bake(this, this::getTextureSprite, modelState, resourceLocation);
    }

    public TextureAtlasSprite getTextureSprite(Material material) {
        return manager.getAtlas(material.atlasLocation()).getSprite(material.texture());
    }
}
