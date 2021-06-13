package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.VerticalSlabItemCacheKey;
import cjminecraft.doubleslabs.client.util.vertex.VerticalSlabTransformer;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class VerticalSlabItemBakedModel implements IBakedModel {

    private static final Cache<VerticalSlabItemCacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build();

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
        public IBakedModel func_239290_a_(IBakedModel parent, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
//            IBakedModel model = new VerticalSlabItemBakedModel(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(slabStack), slabStack);
            return ClientConstants.getVerticalModel(VerticalSlabItem.getStack(stack).getItem());
        }
    }
}
