package cjminecraft.doubleslabs.forge.client.model;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ForgeDynamicSlabBakedModel extends DynamicSlabBakedModel {

    public static final ModelProperty<IDynamicSlabStateContainer> DYNAMIC_SLAB_STATE_CONTAINER = new ModelProperty<>();

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return Collections.emptyList();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        final var model = data.has(DYNAMIC_SLAB_STATE_CONTAINER) ?
                Objects.requireNonNull(data.get(DYNAMIC_SLAB_STATE_CONTAINER)).callOnBlockState(Half.TOP,
                        state -> Minecraft.getInstance().getBlockRenderer().getBlockModel(state),
                        DynamicSlabBakedModel::getFallbackModel) : getFallbackModel();

        return model.getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        if (data.has(DYNAMIC_SLAB_STATE_CONTAINER)) {
            final var stateContainer = Objects.requireNonNull(data.get(DYNAMIC_SLAB_STATE_CONTAINER));
            final var renderDispatcher = Minecraft.getInstance().getBlockRenderer();

            final var renderTypes = new HashSet<RenderType>();
            stateContainer.runOnBlockStates(slabState -> {
                final var model = renderDispatcher.getBlockModel(slabState);
                renderTypes.addAll(model.getRenderTypes(state, rand, data).asList());
            });

            return ChunkRenderTypeSet.of(renderTypes);
        }

        return ChunkRenderTypeSet.all();
    }
}
