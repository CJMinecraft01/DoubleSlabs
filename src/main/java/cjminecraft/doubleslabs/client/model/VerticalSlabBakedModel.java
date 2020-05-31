package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.Config;
import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class VerticalSlabBakedModel extends DoubleSlabBakedModel {

    private static final Quaternion NORTH_ROTATION = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion SOUTH_ROTATION = Vector3f.XN.rotationDegrees(90);
    private static final Quaternion WEST_ROTATION = Vector3f.ZN.rotationDegrees(90);
    private static final Quaternion EAST_ROTATION = Vector3f.ZP.rotationDegrees(90);

    private final Map<String, List<BakedQuad>> cache = new HashMap<>();

    private int[] rotateVertexData(int[] vertexData, Direction direction) {
        int[] data = new int[vertexData.length];
        for (int i = 0; i < vertexData.length / 8; i++) {
            // The x y z position centered at the center of the shape
            float x = Float.intBitsToFloat(vertexData[i * 8]) - 0.5f;
            float y = Float.intBitsToFloat(vertexData[i * 8 + 1]) - 0.5f;
            float z = Float.intBitsToFloat(vertexData[i * 8 + 2]) - 0.5f;
            Vector3f vec = new Vector3f(x, y, z);
            switch (direction) {
                case NORTH:
                    vec.transform(NORTH_ROTATION);
                    break;
                case SOUTH:
                    vec.transform(SOUTH_ROTATION);
                    break;
                case WEST:
                    vec.transform(WEST_ROTATION);
                    break;
                case EAST:
                    vec.transform(EAST_ROTATION);
                    break;
                default:
                    break;
            }
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
        return data;
    }

    private Direction getSideForDirection(Direction side, Direction direction) {
        if (side == null)
            return null;
        if (side.getAxis() == (direction.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X))
            return side;
        if (side == Direction.UP)
            return direction;
        if (side == Direction.DOWN)
            return direction.getOpposite();
        if (side.getAxisDirection() == Direction.AxisDirection.POSITIVE)
            return Direction.UP;
        return Direction.DOWN;
    }

    private List<BakedQuad> getQuadsForState(@Nullable BlockState state, @Nullable Direction side, Random rand, @Nonnull IModelData extraData, int tintOffset, @Nonnull Direction direction) {
        if (state == null) return new ArrayList<>();
        DoubleSlabs.LOGGER.info(side);
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(state);
        return model.getQuads(state, getSideForDirection(side, direction), rand, extraData).stream().map(quad -> new BakedQuad(rotateVertexData(quad.getVertexData(), direction), quad.hasTintIndex() ? quad.getTintIndex() + tintOffset : -1, quad.getFace(), quad.func_187508_a(), quad.shouldApplyDiffuseLighting())).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(TileEntityVerticalSlab.NEGATIVE_STATE) && extraData.hasProperty(TileEntityVerticalSlab.POSITIVE_STATE)) {
            BlockState negativeState = extraData.getData(TileEntityVerticalSlab.NEGATIVE_STATE);
            BlockState positiveState = extraData.getData(TileEntityVerticalSlab.POSITIVE_STATE);
            String cacheKey = Config.slabToString(negativeState) + "," + Config.slabToString(positiveState) +
                    ":" + (side != null ? side.getName() : "null") + ":" +
                    (MinecraftForgeClient.getRenderLayer() != null ? MinecraftForgeClient.getRenderLayer().toString() : "null");
            if (!cache.containsKey(cacheKey)) {
                Direction direction = state.get(BlockVerticalSlab.FACING);
                
                boolean negativeTransparent = negativeState != null && Utils.isTransparent(negativeState);
                boolean positiveTransparent = positiveState != null && Utils.isTransparent(positiveState);
                
                List<BakedQuad> quads = new ArrayList<>();
                if (RenderTypeLookup.canRenderInLayer(positiveState, MinecraftForgeClient.getRenderLayer())) {
                    List<BakedQuad> positiveQuads = getQuadsForState(positiveState, side, rand, extraData, 0, direction);
//                    if (negativeState != null && ((!negativeTransparent && !positiveTransparent) || (positiveTransparent && !negativeTransparent) || (positiveTransparent && negativeTransparent)))
//                        positiveQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction);
                    quads.addAll(positiveQuads);
                }
                if (RenderTypeLookup.canRenderInLayer(negativeState, MinecraftForgeClient.getRenderLayer())) {
                    List<BakedQuad> negativeQuads = getQuadsForState(negativeState, side, rand, extraData, TINT_OFFSET, direction);
//                    if (positiveState != null && ((!positiveTransparent && !negativeTransparent) || (negativeTransparent && !positiveTransparent) || (positiveTransparent && negativeTransparent)))
//                        negativeQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction.getOpposite());
                    quads.addAll(negativeQuads);
                }

//                cache.put(cacheKey, quads);
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
}
