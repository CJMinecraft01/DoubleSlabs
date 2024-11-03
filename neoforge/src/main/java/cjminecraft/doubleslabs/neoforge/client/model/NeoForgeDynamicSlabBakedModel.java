package cjminecraft.doubleslabs.neoforge.client.model;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class NeoForgeDynamicSlabBakedModel extends DynamicSlabBakedModel {

    public static final ModelProperty<IDynamicSlabStateContainer> DYNAMIC_SLAB_STATE_CONTAINER = new ModelProperty<>();

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return Collections.emptyList();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        final BakedModel model = data.has(DYNAMIC_SLAB_STATE_CONTAINER) ?
                Objects.requireNonNull(data.get(DYNAMIC_SLAB_STATE_CONTAINER)).callOnBlockState(Half.TOP,
                        state -> Minecraft.getInstance().getBlockRenderer().getBlockModel(state),
                        DynamicSlabBakedModel::getFallbackModel) : getFallbackModel();

        return model.getParticleIcon(ModelData.EMPTY);
    }
}
