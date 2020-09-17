package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.QuadTransformer;
import cjminecraft.doubleslabs.client.util.VerticalSlabItemCacheKey;
import cjminecraft.doubleslabs.client.util.vertex.VerticalSlabTransformer;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import cjminecraft.doubleslabs.common.util.Quaternion;
import cjminecraft.doubleslabs.common.util.Vector3f;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class VerticalSlabItemBakedModel implements IBakedModel {

    private static final Cache<VerticalSlabItemCacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().build();

    private static final QuadTransformer TRANSFORMER_2D = new QuadTransformer(DefaultVertexFormats.BLOCK, new TRSRTransformation(new javax.vecmath.Vector3f(0, 1, 0), ClientUtils.convert(new Quaternion(new Vector3f(0, 0, -1), 90, true)), null, null));
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
        return this.stack.getItem() instanceof ItemBlock ? ((ItemBlock) this.stack.getItem()).getBlock() : Blocks.AIR;
    }

    private List<BakedQuad> getQuads(@Nullable EnumFacing side, long rand) {
        if (isGui3d()) {
            EnumFacing rotatedSide = ClientUtils.rotateFace(side, EnumFacing.SOUTH);
            List<BakedQuad> quads = this.baseModel.getQuads(null, rotatedSide, rand);
            if (DSConfig.CLIENT.useLazyModel(getBlock().getStateFromMeta(this.stack.getMetadata()))) {
                if (quads.size() == 0)
                    return new ArrayList<>();
                IBlockState baseState = DSBlocks.VERTICAL_SLAB.getDefaultState().withProperty(VerticalSlabBlock.FACING, EnumFacing.SOUTH);
                TextureAtlasSprite sprite = quads.get(0).getSprite();
                return VerticalSlabBakedModel.INSTANCE.getModel(baseState).getQuads(baseState, side, rand).stream().map(quad -> new BakedQuad(ClientUtils.changeQuadUVs(quad.getVertexData(), quad.getSprite(), sprite), quad.hasTintIndex() ? quad.getTintIndex() : -1, quad.getFace(), sprite, quad.shouldApplyDiffuseLighting(), quad.getFormat())).collect(Collectors.toList());
            }
//            return quads.stream().map(quad -> {
//                BakedQuadBuilder builder = new BakedQuadBuilder();
//                VerticalSlabTransformer transformer = new VerticalSlabTransformer(builder, EnumFacing.SOUTH, side, true);
//                quad.pipe(transformer);
//                return builder.build();
//            }).collect(Collectors.toList());
            if (ClientUtils.areShadersEnabled()) {
                VerticalSlabTransformer transformer = new VerticalSlabTransformer(EnumFacing.SOUTH, side, true);
                return transformer.processMany(quads);
            }
            return quads.stream().map(quad -> {
                int[] vertexData = ClientUtils.rotateVertexData(quad.getVertexData(), EnumFacing.SOUTH, side);
                return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
            }).collect(Collectors.toList());
        } else
            return TRANSFORMER_2D.processMany(this.baseModel.getQuads(null, side, rand));
//        return this.baseModel.getQuads(null, side, rand);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        VerticalSlabItemCacheKey key = new VerticalSlabItemCacheKey(side, rand, this.stack, this.baseModel);
        try {
//            if (false)
//                throw new ExecutionException("", new Throwable());
//            return getQuads(side, rand);
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
        return ItemOverrideList.NONE;
    }

    private static class DynamicItemOverrideList extends ItemOverrideList {

        private HashMap<ItemStack, IBakedModel> cache = new HashMap<>();

        public DynamicItemOverrideList() {
            super(new ArrayList<>());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            ItemStack slabStack = VerticalSlabItem.getStack(stack);
            if (cache.containsKey(slabStack))
                return cache.get(slabStack);
            IBakedModel model = new VerticalSlabItemBakedModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(slabStack), slabStack);
            cache.put(slabStack, model);
            return model;
        }
    }
}
