package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class VerticalSlabItemBakedModel implements IBakedModel {

    public static VerticalSlabItemBakedModel INSTANCE;

    private final IBakedModel baseModel;

    public VerticalSlabItemBakedModel(IBakedModel baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return getFallbackModel().getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.baseModel.isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return this.baseModel.func_230044_c_();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.baseModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        if (this == INSTANCE)
            return new DynamicItemOverrideList();
        return ItemOverrideList.EMPTY;
    }

    private static class DynamicItemOverrideList extends ItemOverrideList {

        private HashMap<ItemStack, IBakedModel> cache = new HashMap<>();

        @Nullable
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
            return ClientConstants.getVerticalModel(VerticalSlabItem.getStack(stack).getItem());
        }
    }
}
