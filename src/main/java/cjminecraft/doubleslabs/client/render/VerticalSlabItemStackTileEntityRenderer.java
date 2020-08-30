package cjminecraft.doubleslabs.client.render;

import cjminecraft.doubleslabs.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.VerticalSlabItemCacheKey;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class VerticalSlabItemStackTileEntityRenderer extends ItemStackTileEntityRenderer {

    // north rotation
    private static final Quaternion ROTATION = Vector3f.XP.rotationDegrees(90);
    private static final BlockState BASE_STATE = DSBlocks.VERTICAL_SLAB.get().getDefaultState().with(VerticalSlabBlock.FACING, Direction.SOUTH).with(VerticalSlabBlock.DOUBLE, false);

    private final Cache<VerticalSlabItemCacheKey, List<BakedQuad>> cache = CacheBuilder.newBuilder().maximumSize(1000).build();

    private List<BakedQuad> getQuadsForStack(VerticalSlabItemCacheKey key) {
        if (key.getModel().isGui3d()) {
//        IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(key.getStack(), null, null);
            Direction rotatedSide = ClientUtils.rotateFace(key.getSide(), Direction.SOUTH);
            List<BakedQuad> quads = key.getModel().getQuads(key.getState(), rotatedSide, key.getRandom(), key.getModelData());
            if (key.getStack().getItem() instanceof BlockItem) {
                Block block = ((BlockItem) key.getStack().getItem()).getBlock();
                if (DSConfig.CLIENT.useLazyModel(block)) {
                    if (quads.size() == 0)
                        return new ArrayList<>();
                    TextureAtlasSprite sprite = quads.get(0).func_187508_a();
                    return VerticalSlabBakedModel.INSTANCE.getModel(BASE_STATE).getQuads(BASE_STATE, key.getSide(), key.getRandom(), EmptyModelData.INSTANCE).stream().map(quad -> new BakedQuad(ClientUtils.changeQuadUVs(quad.getVertexData(), quad.func_187508_a(), sprite), quad.hasTintIndex() ? quad.getTintIndex() : -1, quad.getFace(), sprite, quad.func_239287_f_())).collect(Collectors.toList());
                }
            }
            return quads.stream().map(quad -> {
                int[] vertexData = ClientUtils.rotateVertexData(quad.getVertexData(), Direction.SOUTH, key.getSide());
                return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.func_187508_a(), quad.func_239287_f_());
            }).collect(Collectors.toList());
        } else {
            // TODO rotate 2d item model
            return key.getModel().getQuads(null, key.getSide(), key.getRandom(), key.getModelData());
        }
    }

    public void renderModel(IBakedModel model, ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn) {
        Random random = new Random();
        long i = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(i);
            VerticalSlabItemCacheKey key = new VerticalSlabItemCacheKey(direction, random, stack, model);
            renderModelQuads(stack, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, key);
        }

        random.setSeed(i);
        VerticalSlabItemCacheKey key = new VerticalSlabItemCacheKey(null, random, stack, model);
        renderModelQuads(stack, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, key);
    }

    private void renderModelQuads(ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn, VerticalSlabItemCacheKey key) {
        List<BakedQuad> quads;
        try {
            if (false)
                throw new ExecutionException("", new Throwable());
//            if (key.getSide() != Direction.UP)
            quads = getQuadsForStack(key);
//            else quads = new ArrayList<>();
//            quads = cache.get(key, () -> getQuadsForStack(key));
        } catch (ExecutionException e) {
            quads = new ArrayList<>();
            DoubleSlabs.LOGGER.debug("Caught error when getting quads for key {}", key);
            DoubleSlabs.LOGGER.catching(Level.DEBUG, e);
        }
//        if (quads.size() > 0)
        Minecraft.getInstance().getItemRenderer().renderQuads(matrixStackIn, bufferIn, quads, stack, combinedLightIn, combinedOverlayIn);
    }

    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, boolean leftHand, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (!stack.isEmpty()) {
            matrixStack.push();
            boolean flag = transformType == ItemCameraTransforms.TransformType.GUI || transformType == ItemCameraTransforms.TransformType.GROUND || transformType == ItemCameraTransforms.TransformType.FIXED;
            IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
//            if (stack.getItem() instanceof BlockItem && model.isGui3d())
//                model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(((BlockItem) stack.getItem()).getBlock().getDefaultState());
//            matrixStack.translate(0.5D, 0.5D, 0.5D);
            model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHand);
            if (!model.isBuiltInRenderer() && (stack.getItem() != Items.TRIDENT || flag)) {
                boolean flag1;
                if (transformType != ItemCameraTransforms.TransformType.GUI && !transformType.func_241716_a_() && stack.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem)stack.getItem()).getBlock();
                    flag1 = !(block instanceof BreakableBlock) && !(block instanceof StainedGlassPaneBlock);
                } else {
                    flag1 = true;
                }
                if (model.isLayered()) { net.minecraftforge.client.ForgeHooksClient.drawItemLayered(Minecraft.getInstance().getItemRenderer(), model, stack, matrixStack, buffer, combinedLight, combinedOverlay, flag1); }
                else {
                    RenderType rendertype = RenderTypeLookup.func_239219_a_(stack, flag1);
                    IVertexBuilder ivertexbuilder;
                    if (stack.getItem() == Items.COMPASS && stack.hasEffect()) {
                        matrixStack.push();
                        MatrixStack.Entry matrixstack$entry = matrixStack.getLast();
                        if (transformType == ItemCameraTransforms.TransformType.GUI) {
                            matrixstack$entry.getMatrix().mul(0.5F);
                        } else if (transformType.func_241716_a_()) {
                            matrixstack$entry.getMatrix().mul(0.75F);
                        }

                        if (flag1) {
                            ivertexbuilder = ItemRenderer.func_241732_b_(buffer, rendertype, matrixstack$entry);
                        } else {
                            ivertexbuilder = ItemRenderer.func_241731_a_(buffer, rendertype, matrixstack$entry);
                        }

                        matrixStack.pop();
                    } else if (flag1) {
                        ivertexbuilder = ItemRenderer.func_239391_c_(buffer, rendertype, true, stack.hasEffect());
//                        ivertexbuilder = ItemRenderer.getBuffer(buffer, rendertype, true, stack.hasEffect());
                    } else {
                        ivertexbuilder = ItemRenderer.getBuffer(buffer, rendertype, true, stack.hasEffect());
                    }

                    matrixStack.push();

                    if (!model.isGui3d()) {
                        if (transformType == ItemCameraTransforms.TransformType.GUI) {
                            matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
                            matrixStack.translate(0, -1, 0);
                        }
                    } else {
                        if (transformType == ItemCameraTransforms.TransformType.GUI) {
                            matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
                            matrixStack.translate(1.5, 1.5, 0);
                            matrixStack.scale(1.05f, 1.05f, 1.05f);
                        } else if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
//                            matrixStack.rotate(Vector3f.YP.rotationDegrees(-90));
//                            matrixStack.scale(2, 2, 2);
                        }
                    }
                    this.renderModel(model, stack, combinedLight, combinedOverlay, matrixStack, ivertexbuilder);
                    matrixStack.pop();
                }
            } else {
                stack.getItem().getItemStackTileEntityRenderer().func_239207_a_(stack, transformType, matrixStack, buffer, combinedLight, combinedOverlay);
            }

            matrixStack.pop();
        }
    }

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ItemStack slabStack = VerticalSlabItem.getStack(stack);
        renderItem(slabStack, transformType, false, matrixStack, buffer, combinedLight, combinedOverlay);

//        IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(slabStack, null, null);
//        matrixStack.push();
//        if (model.isGui3d()) {
//            matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
////            matrixStack.rotate(Vector3f.YP.rotationDegrees(180));
////                    matrixStack.rotate(Vector3f.XP.rotationDegrees(30));
////            matrixStack.rotate(Vector3f.YP.rotationDegrees(45));
////            matrixStack.translate(0.5f, -0.5f, 0);
//        } else {
//            matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
//        }
//        switch (transformType) {
//            case THIRD_PERSON_LEFT_HAND:
//                break;
//            case THIRD_PERSON_RIGHT_HAND:
//                break;
//            case FIRST_PERSON_LEFT_HAND:
//                break;
//            case FIRST_PERSON_RIGHT_HAND:
//                if (!model.isGui3d()) {
//                    matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
//                    matrixStack.rotate(Vector3f.ZN.rotationDegrees(90));
//                }
//                matrixStack.translate(0.5f, -0.5f, 0.5f);
//                break;
//            case HEAD:
//                break;
//            case GUI:
//                if (model.isGui3d())
//                    matrixStack.translate(0.5f, -0.5f, 0);
//                else
//                    matrixStack.translate(0.5f, -0.5f, 0);
//                break;
//            case GROUND:
//                matrixStack.translate(0.5f, -0.5f, 0.5f);
//                break;
//            case FIXED:
//                break;
//            default:
//                break;
//        }
//
////        matrixStack.rotate(ROTATION);
//
////        matrixStack.translate(0.0, 0.0, -1.0);
//
//        Minecraft.getInstance().getItemRenderer().renderItem(slabStack, transformType, combinedLight, combinedOverlay, matrixStack, buffer);
//        matrixStack.pop();
    }
}
