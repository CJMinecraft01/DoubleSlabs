package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.VerticalSlabItemCacheKey;
import cjminecraft.doubleslabs.client.util.vertex.VerticalSlabTransformer;
import cjminecraft.doubleslabs.common.DoubleSlabs;
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
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class VerticalSlabItemBakedModel implements IBakedModel {

    private static final Cache<VerticalSlabItemCacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build();

    private static final QuadTransformer TRANSFORMER_2D = new QuadTransformer(new TransformationMatrix(new Vector3f(0, 1, 0), Vector3f.ZN.rotationDegrees(90), null, null));
    public static VerticalSlabItemBakedModel INSTANCE;

    private final ItemStack stack;
    private final IBakedModel baseModel;

    public IBakedModel getBaseModel() {
        return this.baseModel;
    }

    public VerticalSlabItemBakedModel(IBakedModel baseModel) {
        this(baseModel, ItemStack.EMPTY);
    }

    public VerticalSlabItemBakedModel(IBakedModel baseModel, ItemStack stack) {
        this.baseModel = baseModel;
        this.stack = stack;
    }

    private Block getBlock() {
        return this.stack.getItem() instanceof BlockItem ? ((BlockItem) this.stack.getItem()).getBlock() : Blocks.AIR;
    }

    private List<BakedQuad> getQuads(@Nullable Direction side, Random rand) {
        if (isGui3d()) {
            Direction rotatedSide = ClientUtils.rotateFace(side, Direction.SOUTH);
            List<BakedQuad> quads = this.baseModel.getQuads(null, rotatedSide, rand);
            if (DSConfig.CLIENT.useLazyModel(getBlock())) {
                if (quads.size() == 0)
                    return new ArrayList<>();
                BlockState baseState = DSBlocks.VERTICAL_SLAB.get().getDefaultState().with(VerticalSlabBlock.FACING, Direction.SOUTH);
                TextureAtlasSprite sprite = quads.get(0).func_187508_a();
                return VerticalSlabBakedModel.INSTANCE.getModel(baseState).getQuads(baseState, side, rand, EmptyModelData.INSTANCE).stream().map(quad -> new BakedQuad(ClientUtils.changeQuadUVs(quad.getVertexData(), quad.func_187508_a(), sprite), quad.hasTintIndex() ? quad.getTintIndex() : -1, quad.getFace(), sprite, quad.shouldApplyDiffuseLighting())).collect(Collectors.toList());
            }
//            return quads.stream().map(quad -> {
//                BakedQuadBuilder builder = new BakedQuadBuilder();
//                VerticalSlabTransformer transformer = new VerticalSlabTransformer(builder, Direction.SOUTH, side, true);
//                quad.pipe(transformer);
//                return builder.build();
//            }).collect(Collectors.toList());
            if (ClientUtils.areShadersEnabled()) {
                VerticalSlabTransformer transformer = new VerticalSlabTransformer(Direction.SOUTH, side, true);
                return transformer.processMany(quads);
            }
            return quads.stream().map(quad -> {
                int[] vertexData = ClientUtils.rotateVertexData(quad.getVertexData(), Direction.SOUTH, side);
                return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.func_187508_a(), quad.shouldApplyDiffuseLighting());
            }).collect(Collectors.toList());
        } else
            return TRANSFORMER_2D.processMany(this.baseModel.getQuads(null, side, rand));
//        return this.baseModel.getQuads(null, side, rand);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        VerticalSlabItemCacheKey key = new VerticalSlabItemCacheKey(side, rand, this.stack, this.baseModel);
        try {
//                if (false)
//                    throw new ExecutionException("", new Throwable());
//                return getQuads(side, rand);
            return cache.get(key, () -> getQuads(side, rand));
        } catch (ExecutionException e) {
            DoubleSlabs.LOGGER.debug("Caught error when getting quads for key {}", key);
            DoubleSlabs.LOGGER.catching(Level.DEBUG, e);
        }
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
        public IBakedModel getModelWithOverrides(IBakedModel parent, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
            ItemStack slabStack = VerticalSlabItem.getStack(stack);
            if (cache.containsKey(slabStack))
                return cache.get(slabStack);
            IBakedModel model = new VerticalSlabItemBakedModel(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getItemModel(slabStack), slabStack);
            cache.put(slabStack, model);
            return model;
        }
    }
}
