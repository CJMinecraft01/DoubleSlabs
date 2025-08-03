package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.ClientInternal;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static cjminecraft.doubleslabs.client.ClientInternal.getFallbackModel;

public class VerticalSlabItemBakedModel implements BakedModel {

    private final ItemOverrides overrides;

    public VerticalSlabItemBakedModel(ModelBaker baker) {
        this.overrides = new DynamicItemOverrideList(baker);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return getFallbackModel().getQuads(blockState, direction, randomSource);
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
    public ItemOverrides getOverrides() {
        return overrides;
    }

    private static class DynamicItemOverrideList extends ItemOverrides {

        public DynamicItemOverrideList(ModelBaker baker) {
            //noinspection DataFlowIssue the model parameter will never be used if the overrides is empty
            super(baker, null, List.of());
        }

        @Override
        public @Nullable BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            final var slabStack = VerticalSlabItem.getContainedSlabItem(stack);

            if (!slabStack.isEmpty()) {
                return ClientInternal.getVerticalSlabModelHelper().getVerticalSlabModel(slabStack);
            }

            return getFallbackModel();
        }
    }
}
