package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class VerticalSlabBakedModel extends DoubleSlabBakedModel {

    public static final ModelProperty<Boolean> ROTATE_POSITIVE = new ModelProperty<>();
    public static final ModelProperty<Boolean> ROTATE_NEGATIVE = new ModelProperty<>();

    private static final Quaternion NORTH_ROTATION = new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90, true);
    private static final Quaternion SOUTH_ROTATION = new Quaternion(new Vector3f(-1.0F, 0.0F, 0.0F), 90, true);
    private static final Quaternion WEST_ROTATION = new Quaternion(new Vector3f(0.0F, 0.0F, -1.0F), 90, true);
    private static final Quaternion EAST_ROTATION = new Quaternion(new Vector3f(0.0F, 0.0F, 1.0F), 90, true);
    private static final Quaternion ROTATE_X_90 = new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90, true);
    private static final Quaternion ROTATE_Z_180 = new Quaternion(new Vector3f(0.0F, 0.0F, 1.0F), 180, true);

    private final Map<String, List<BakedQuad>> cache = new HashMap<>();

    private static int[] rotateVertexData(int[] vertexData, Direction direction, @Nullable Direction side, boolean positiveState) {
        int[] data = new int[vertexData.length];
        int[] vertexOrder = new int[vertexData.length / 7];
        for (int i = 0; i < vertexData.length / 7; i++) {
            // The x y z position centered at the center of the shape
            float x = Float.intBitsToFloat(vertexData[i * 7]) - 0.5f;
            float y = Float.intBitsToFloat(vertexData[i * 7 + 1]) - 0.5f;
            float z = Float.intBitsToFloat(vertexData[i * 7 + 2]) - 0.5f;

            Vector3f vec = new Vector3f(x, y, z);
            vertexOrder[i] = i;
            switch (direction) {
                case NORTH:
                    if (side != null) {
                        if (side == Direction.NORTH)
                            vertexOrder[i] = (i + 2) % 4;
                        else if (side == Direction.SOUTH)
                            vertexOrder[i] = i;
                        else if (side == Direction.WEST)
                            vertexOrder[i] = (i + 1) % 4;
                        else
                            vertexOrder[i] = (i + 3) % 4;
                    }
                    vec.func_214905_a(NORTH_ROTATION);
                    break;
                case SOUTH:
                    if (side != null) {
                        if (side == Direction.NORTH)
                            vertexOrder[i] = i;
                        else if (side == Direction.SOUTH)
                            vertexOrder[i] = (i + 2) % 4;
                        else if (side == Direction.WEST)
                            vertexOrder[i] = (i + 3) % 4;
                        else
                            vertexOrder[i] = (i + 1) % 4;
                    }
                    vec.func_214905_a(SOUTH_ROTATION);
                    vec.func_214905_a(ROTATE_Z_180);
                    break;
                case WEST:
                    if (side != null) {
                        if (side == Direction.NORTH)
                            vertexOrder[i] = (i + 3) % 4;
                        else if (side == Direction.SOUTH)
                            vertexOrder[i] = (i + 1) % 4;
                        else if (side == Direction.EAST)
                            vertexOrder[i] = i % 4;
                        else
                            vertexOrder[i] = (i + 2) % 4;
                    }
                    vec.func_214905_a(WEST_ROTATION);
                    vec.func_214905_a(ROTATE_X_90);
                    break;
                case EAST:
                    if (side != null) {
                        if (side == Direction.NORTH)
                            vertexOrder[i] = (i + 1) % 4;
                        else if (side == Direction.SOUTH)
                            vertexOrder[i] = (i + 3) % 4;
                        else if (side == Direction.EAST)
                            vertexOrder[i] = (i + 2) % 4;
                        else
                            vertexOrder[i] = i % 4;
                    }
                    vec.func_214905_a(EAST_ROTATION);
                    vec.func_214905_a(ROTATE_X_90);
                    break;
                default:
                    break;
            }

            if (side == null)
                vertexOrder[i] = i;

            float transformedX = vec.getX() + 0.5f;
            float transformedY = vec.getY() + 0.5f;
            float transformedZ = vec.getZ() + 0.5f;

            data[i * 7] = Float.floatToRawIntBits(transformedX);
            data[i * 7 + 1] = Float.floatToRawIntBits(transformedY);
            data[i * 7 + 2] = Float.floatToRawIntBits(transformedZ);
            data[i * 7 + 3] = vertexData[i * 7 + 3]; // shade colour
            data[i * 7 + 4] = vertexData[i * 7 + 4]; // texture U
            data[i * 7 + 5] = vertexData[i * 7 + 5]; // texture V
        }

        int[] finalData = new int[data.length];
        for (int i = 0; i < vertexOrder.length; i++) {
            int j = !Utils.isOptiFineInstalled() ? vertexOrder[i] * 7 : i * 7;
            finalData[i * 7] = data[j];
            finalData[i * 7 + 1] = data[j + 1];
            finalData[i * 7 + 2] = data[j + 2];
            finalData[i * 7 + 3] = data[j + 3];
            finalData[i * 7 + 4] = data[j + 4];
            finalData[i * 7 + 5] = data[j + 5];
        }

        ForgeHooksClient.fillNormal(finalData, direction);

//        DoubleSlabs.LOGGER.info(direction.getName() + " " + FaceBakery.getFacingFromVertexData(vertexData).getName() + " " + FaceBakery.getFacingFromVertexData(data));
        return finalData;
    }

//    private Direction getSideForDirection(Direction side, Direction direction) {
//        if (side == null)
//            return null;
//        if (side.getAxis() == (direction.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X))
//            return side;
//        if (side == Direction.UP)
//            return direction;
//        if (side == Direction.DOWN)
//            return direction.getOpposite();
//        if (side.getAxisDirection() == Direction.AxisDirection.POSITIVE)
//            return Direction.UP;
//        return Direction.DOWN;
//    }

    private List<BakedQuad> getQuadsForState(@Nullable BlockState state, @Nullable Direction side, Random rand, @Nonnull IModelData extraData, int tintOffset, @Nonnull Direction direction, boolean positiveState) {
        if (state == null) return new ArrayList<>();
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        if ((positiveState && !extraData.getData(ROTATE_POSITIVE)) || (!positiveState && !extraData.getData(ROTATE_NEGATIVE)))
            return new ArrayList<>(model.getQuads(state, side, rand, extraData));
        return new ArrayList<>(model.getQuads(state, Utils.rotateFace(side, direction), rand, extraData).stream().map(quad -> {
            int[] vertexData = rotateVertexData(quad.getVertexData(), direction, side, positiveState);
            return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() + tintOffset : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
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
                    ":" + (side != null ? side.getName() : "null") + ":" +
                    (MinecraftForgeClient.getRenderLayer() != null ? MinecraftForgeClient.getRenderLayer().toString() : "null") + "," + direction.getName();
            if (!cache.containsKey(cacheKey)) {
                boolean negativeTransparent = negativeState != null && Utils.isTransparent(negativeState);
                boolean positiveTransparent = positiveState != null && Utils.isTransparent(positiveState);
                
                List<BakedQuad> quads = new ArrayList<>();
                if (positiveState != null && MinecraftForgeClient.getRenderLayer() == positiveState.getBlock().getRenderLayer()) {
                    List<BakedQuad> positiveQuads = getQuadsForState(positiveState, side, rand, extraData, 0, direction, true);
                    if (negativeState != null && ((!negativeTransparent && !positiveTransparent) || (positiveTransparent && !negativeTransparent) || (positiveTransparent && negativeTransparent)))
                        positiveQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction.getOpposite());
                    quads.addAll(positiveQuads);
                }
                if (negativeState != null && MinecraftForgeClient.getRenderLayer() == negativeState.getBlock().getRenderLayer()) {
                    List<BakedQuad> negativeQuads = getQuadsForState(negativeState, side, rand, extraData, TINT_OFFSET, direction, false);
                    if (positiveState != null && ((!positiveTransparent && !negativeTransparent) || (negativeTransparent && !positiveTransparent) || (positiveTransparent && negativeTransparent)))
                        negativeQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction);
                    quads.addAll(negativeQuads);
                }

                cache.put(cacheKey, quads);
                return quads;
            } else {
                return cache.get(cacheKey);
            }
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
    public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        boolean rotatePositive = true;
        boolean rotateNegative = true;
        if (tileData.getData(TileEntityVerticalSlab.POSITIVE_STATE) != null) {
            ISlabSupport positiveSlabSupport = SlabSupport.getVerticalSlabSupport(world, pos, tileData.getData(TileEntityVerticalSlab.POSITIVE_STATE));
            rotatePositive = positiveSlabSupport == null;
        }
        if (tileData.getData(TileEntityVerticalSlab.NEGATIVE_STATE) != null) {
            ISlabSupport negativeSlabSupport = SlabSupport.getVerticalSlabSupport(world, pos, tileData.getData(TileEntityVerticalSlab.NEGATIVE_STATE));
            rotateNegative = negativeSlabSupport == null;
        }
        tileData.setData(ROTATE_POSITIVE, rotatePositive);
        tileData.setData(ROTATE_NEGATIVE, rotateNegative);
        return tileData;
    }
}
