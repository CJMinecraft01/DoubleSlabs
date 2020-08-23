package cjminecraft.doubleslabs.old.client.model;

import cjminecraft.doubleslabs.old.Config;
import cjminecraft.doubleslabs.old.Utils;
import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupportOld;
import cjminecraft.doubleslabs.old.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.old.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class VerticalSlabBakedModel extends DoubleSlabBakedModel {

    public static final ModelProperty<Boolean> ROTATE_POSITIVE = new ModelProperty<>();
    public static final ModelProperty<Boolean> ROTATE_NEGATIVE = new ModelProperty<>();

    private static final Quaternion NORTH_ROTATION = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion SOUTH_ROTATION = Vector3f.XN.rotationDegrees(90);
    private static final Quaternion WEST_ROTATION = Vector3f.ZN.rotationDegrees(90);
    private static final Quaternion EAST_ROTATION = Vector3f.ZP.rotationDegrees(90);
    private static final Quaternion ROTATE_X_90 = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion ROTATE_X_270 = Vector3f.XP.rotationDegrees(270);
    private static final Quaternion ROTATE_Z_180 = Vector3f.ZP.rotationDegrees(180);

    private final Map<String, List<BakedQuad>> cache = new HashMap<>();

    private final Map<BlockState, IBakedModel> models = new HashMap<>();

    public void addModel(IBakedModel model, BlockState state) {
        this.models.put(state, model);
    }

    private static int[] rotateVertexData(int[] vertexData, Direction direction, @Nullable Direction side) {
        int[] data = new int[vertexData.length];
        int[] vertexOrder = new int[vertexData.length / 8];
        for (int i = 0; i < vertexData.length / 8; i++) {
            // The x y z position centered at the center of the shape
            float x = Float.intBitsToFloat(vertexData[i * 8]) - 0.5f;
            float y = Float.intBitsToFloat(vertexData[i * 8 + 1]) - 0.5f;
            float z = Float.intBitsToFloat(vertexData[i * 8 + 2]) - 0.5f;

            Vector3f vec = new Vector3f(x, y, z);
            vertexOrder[i] = i;
            switch (direction) {
                case NORTH:
                    if (side != null) {
                        if (side == Direction.NORTH)
                            vertexOrder[i] = i % 4;
                        else if (side == Direction.SOUTH)
                            vertexOrder[i] = (i + 2) % 4;
                        else if (side == Direction.WEST)
                            vertexOrder[i] = (i + 1) % 4;
                        else if (side == Direction.EAST)
                            vertexOrder[i] = (i + 3) % 4;
                        else if (side == Direction.UP)
                            vertexOrder[i] = (i + 2) % 4;
                        else
                            vertexOrder[i] = i % 4;
                    }
                    vec.transform(NORTH_ROTATION);
                    if (side == direction)
                        vec.transform(ROTATE_Z_180);
                    break;
                case SOUTH:
                    if (side != null) {
                        if (side == Direction.NORTH)
                            vertexOrder[i] = (i + 2) % 4;
                        else if (side == Direction.SOUTH)
                            vertexOrder[i] = i % 4;
                        else if (side == Direction.WEST)
                            vertexOrder[i] = (i + 3) % 4;
                        else if (side == Direction.EAST)
                            vertexOrder[i] = (i + 1) % 4;
                        else if (side == Direction.UP)
                            vertexOrder[i] = i % 4;
                        else
                            vertexOrder[i] = (i + 2) % 4;
                    }
                    vec.transform(SOUTH_ROTATION);
                    if (side != direction)
                        vec.transform(ROTATE_Z_180);
                    break;
                case WEST:
                    if (side != null) {
                        if (side == Direction.NORTH)
                            vertexOrder[i] = (i + 3) % 4;
                        else if (side == Direction.SOUTH)
                            vertexOrder[i] = (i + 1) % 4;
                        else if (side == Direction.EAST)
                            vertexOrder[i] = (i + 2) % 4;
                        else
                            vertexOrder[i] = i % 4;
                    }
                    vec.transform(WEST_ROTATION);
                    if (side != direction)
                        vec.transform(ROTATE_X_90);
                    else
                        vec.transform(ROTATE_X_270);
                    break;
                case EAST:
                    if (side != null) {
                        if (side == Direction.NORTH)
                            vertexOrder[i] = (i + 1) % 4;
                        else if (side == Direction.SOUTH)
                            vertexOrder[i] = (i + 3) % 4;
                        else if (side == Direction.EAST)
                            vertexOrder[i] = i % 4;
                        else
                            vertexOrder[i] = (i + 2) % 4;
                    }
                    vec.transform(EAST_ROTATION);
                    if (side != direction)
                        vec.transform(ROTATE_X_90);
                    else
                        vec.transform(ROTATE_X_270);
                    break;
                default:
                    break;
            }

            if (side == null)
                vertexOrder[i] = i;

            float transformedX = vec.getX() + 0.5f;
            float transformedY = vec.getY() + 0.5f;
            float transformedZ = vec.getZ() + 0.5f;

            data[i * 8] = Float.floatToRawIntBits(transformedX);
            data[i * 8 + 1] = Float.floatToRawIntBits(transformedY);
            data[i * 8 + 2] = Float.floatToRawIntBits(transformedZ);
            data[i * 8 + 3] = vertexData[i * 8 + 3]; // shade colour
            data[i * 8 + 4] = vertexData[i * 8 + 4]; // texture U
            data[i * 8 + 5] = vertexData[i * 8 + 5]; // texture V
            data[i * 8 + 6] = vertexData[i * 8 + 6]; // baked lighting
            data[i * 8 + 7] = vertexData[i * 8 + 7]; // normal
        }

        int[] finalData = new int[data.length];
        for (int i = 0; i < vertexOrder.length; i++) {
            int j = !Utils.isOptiFineInstalled() ? vertexOrder[i] * 8 : i * 8;
//            int j = i * 8;
            finalData[i * 8] = data[j];
            finalData[i * 8 + 1] = data[j + 1];
            finalData[i * 8 + 2] = data[j + 2];
            finalData[i * 8 + 3] = data[j + 3];
            finalData[i * 8 + 4] = data[j + 4];
            finalData[i * 8 + 5] = data[j + 5];
            finalData[i * 8 + 6] = data[j + 6];
            finalData[i * 8 + 7] = data[j + 7];
        }

        ForgeHooksClient.fillNormal(finalData, direction);

//        DoubleSlabs.LOGGER.info(direction.getName() + " " + FaceBakery.getFacingFromVertexData(vertexData).getName() + " " + FaceBakery.getFacingFromVertexData(data));
        return finalData;
    }

    private int[] changeUVs(int[] vertexData, TextureAtlasSprite originalSprite, TextureAtlasSprite newSprite) {
        int[] data = new int[vertexData.length];
        for (int i = 0; i < vertexData.length / 8; i ++) {
            data[i * 8] = vertexData[i * 8]; // x
            data[i * 8 + 1] = vertexData[i * 8 + 1]; // y
            data[i * 8 + 2] = vertexData[i * 8 + 2]; // z
            data[i * 8 + 3] = vertexData[i * 8 + 3]; // shade colour
            data[i * 8 + 4] = Float.floatToRawIntBits(Float.intBitsToFloat(vertexData[i * 8 + 4]) - originalSprite.getMinU() + newSprite.getMinU()); // texture U
            data[i * 8 + 5] = Float.floatToRawIntBits(Float.intBitsToFloat(vertexData[i * 8 + 5]) - originalSprite.getMinV() + newSprite.getMinV()); // texture V
            data[i * 8 + 6] = vertexData[i * 8 + 6]; // baked lighting
            data[i * 8 + 7] = vertexData[i * 8 + 7]; // normal
        }
        return data;
    }

    private List<BakedQuad> getQuadsForState(@Nullable BlockState state, @Nullable Direction side, Random rand, @Nonnull IModelData extraData, int tintOffset, BlockState slabState, boolean positiveState) {
        if (state == null) return new ArrayList<>();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        if ((positiveState && !extraData.getData(ROTATE_POSITIVE)) || (!positiveState && !extraData.getData(ROTATE_NEGATIVE)))
            return new ArrayList<>(model.getQuads(state, side, rand, extraData));
        Direction direction = slabState.get(BlockVerticalSlab.FACING);
        List<BakedQuad> quads = model.getQuads(state, Utils.rotateFace(side, direction), rand, EmptyModelData.INSTANCE);
        if (Config.useLazyModel(state.getBlock())) {
            BlockState state1 = (positiveState ? slabState : slabState.with(BlockVerticalSlab.FACING, direction.getOpposite())).with(BlockVerticalSlab.DOUBLE, false);
            if (quads.size() == 0)
                return new ArrayList<>();
            TextureAtlasSprite sprite = quads.get(0).func_187508_a();
            return new ArrayList<>(this.models.get(state1).getQuads(state1, side, rand, EmptyModelData.INSTANCE).stream().map(quad -> new BakedQuad(changeUVs(quad.getVertexData(), quad.func_187508_a(), sprite), quad.hasTintIndex() ? quad.getTintIndex() : -1, quad.getFace(), sprite, quad.func_239287_f_())).collect(Collectors.toList()));
        }

        return new ArrayList<>(quads.stream().map(quad -> {
            int[] vertexData = rotateVertexData(positiveState && extraData.getData(OFFSET_Y_POSITIVE) != 0 ? offsetY(quad.getVertexData(), extraData.getData(OFFSET_Y_POSITIVE)): !positiveState && extraData.getData(OFFSET_Y_NEGATIVE) != 0 ? offsetY(quad.getVertexData(), extraData.getData(OFFSET_Y_NEGATIVE)) : quad.getVertexData(), direction, side);
            return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() + tintOffset : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.func_187508_a(), quad.func_239287_f_());
        }).collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(TileEntityVerticalSlab.NEGATIVE_STATE) && extraData.hasProperty(TileEntityVerticalSlab.POSITIVE_STATE)) {
            BlockState negativeState = extraData.getData(TileEntityVerticalSlab.NEGATIVE_STATE);
            BlockState positiveState = extraData.getData(TileEntityVerticalSlab.POSITIVE_STATE);
            Direction direction = state.get(BlockVerticalSlab.FACING);
            String cacheKey = (negativeState != null ? negativeState.toString() : "null") + "," + (positiveState != null ? positiveState.toString() : "null") +
                    ":" + (side != null ? side.getName2() : "null") + ":" +
                    (MinecraftForgeClient.getRenderLayer() != null ? MinecraftForgeClient.getRenderLayer().toString() : "null") + "," + direction.getName2();
            if (!cache.containsKey(cacheKey)) {
                boolean shouldCull = positiveState != null && negativeState != null && Config.shouldCull(positiveState.getBlock()) && Config.shouldCull(negativeState.getBlock());
                boolean negativeTransparent = negativeState != null && Utils.isTransparent(negativeState);
                boolean positiveTransparent = positiveState != null && Utils.isTransparent(positiveState);

                List<BakedQuad> quads = new ArrayList<>();
                if (positiveState != null && (RenderTypeLookup.canRenderInLayer(positiveState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null)) {
                    List<BakedQuad> positiveQuads = getQuadsForState(positiveState, side, rand, extraData, 0, state, true);
                    if (shouldCull)
                        if ((!negativeTransparent && !positiveTransparent) || (positiveTransparent && !negativeTransparent) || (positiveTransparent && negativeTransparent))
                            positiveQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction.getOpposite());
                    quads.addAll(positiveQuads);
                }
                if (negativeState != null && (RenderTypeLookup.canRenderInLayer(negativeState, MinecraftForgeClient.getRenderLayer()) || MinecraftForgeClient.getRenderLayer() == null)) {
                    List<BakedQuad> negativeQuads = getQuadsForState(negativeState, side, rand, extraData, TINT_OFFSET, state, false);
                    if (shouldCull)
                        if ((!positiveTransparent && !negativeTransparent) || (negativeTransparent && !positiveTransparent) || (positiveTransparent && negativeTransparent))
                            negativeQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction);
                    quads.addAll(negativeQuads);
                }

                cache.put(cacheKey, quads);
                return quads;
            } else {
                return cache.get(cacheKey);
            }
        } else if (MinecraftForgeClient.getRenderLayer() == null) {
            // This should only be called when we are trying to render the breaking animation
            IBakedModel model = this.models.getOrDefault(state, null);
            if (model != null)
                return model.getQuads(state, side, rand, extraData);
        }
        return getFallback().getQuads(state, side, rand, extraData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        if (data.hasProperty(TileEntityVerticalSlab.POSITIVE_STATE) && data.getData(TileEntityVerticalSlab.NEGATIVE_STATE) != null)
            return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(data.getData(TileEntityVerticalSlab.POSITIVE_STATE)).getParticleTexture(data);
        return getFallback().getParticleTexture(data);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        boolean rotatePositive = true;
        boolean rotateNegative = true;
        float offsetYPositive = 0;
        float offsetYNegative = 0;
        if (tileData.getData(TileEntityVerticalSlab.POSITIVE_STATE) != null) {
            ISlabSupport positiveSlabSupport = SlabSupportOld.getVerticalSlabSupport(world, pos, tileData.getData(TileEntityVerticalSlab.POSITIVE_STATE));
            rotatePositive = positiveSlabSupport == null;
            positiveSlabSupport = SlabSupportOld.getHorizontalSlabSupport(world, pos, tileData.getData(TileEntityVerticalSlab.POSITIVE_STATE));
            if (positiveSlabSupport != null)
                offsetYPositive = positiveSlabSupport.getOffsetY(false);
        }
        if (tileData.getData(TileEntityVerticalSlab.NEGATIVE_STATE) != null) {
            ISlabSupport negativeSlabSupport = SlabSupportOld.getVerticalSlabSupport(world, pos, tileData.getData(TileEntityVerticalSlab.NEGATIVE_STATE));
            rotateNegative = negativeSlabSupport == null;
            negativeSlabSupport = SlabSupportOld.getHorizontalSlabSupport(world, pos, tileData.getData(TileEntityVerticalSlab.NEGATIVE_STATE));
            if (negativeSlabSupport != null)
                offsetYNegative = negativeSlabSupport.getOffsetY(true);
        }
        tileData.setData(OFFSET_Y_POSITIVE, offsetYPositive);
        tileData.setData(OFFSET_Y_NEGATIVE, offsetYNegative);
        tileData.setData(ROTATE_POSITIVE, rotatePositive);
        tileData.setData(ROTATE_NEGATIVE, rotateNegative);
        return tileData;
    }
}
