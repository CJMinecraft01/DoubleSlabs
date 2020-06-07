package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.util.Quaternion;
import cjminecraft.doubleslabs.util.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VerticalSlabBakedModel extends DoubleSlabBakedModel {

    public static final ModelResourceLocation variantTag
            = new ModelResourceLocation(new ResourceLocation(DoubleSlabs.MODID, "vertical_slab"), "normal");

    private static final Quaternion NORTH_ROTATION = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion SOUTH_ROTATION = Vector3f.XN.rotationDegrees(90);
    private static final Quaternion WEST_ROTATION = Vector3f.ZN.rotationDegrees(90);
    private static final Quaternion EAST_ROTATION = Vector3f.ZP.rotationDegrees(90);
    private static final Quaternion ROTATE_X_90 = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion ROTATE_Z_180 = Vector3f.ZP.rotationDegrees(180);

    private final Map<String, List<BakedQuad>> cache = new HashMap<>();

    private static int[] rotateVertexData(int[] vertexData, EnumFacing direction, @Nullable EnumFacing side, boolean positiveState) {
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
                        if (side == EnumFacing.NORTH)
                            vertexOrder[i] = (i + 2) % 4;
                        else if (side == EnumFacing.SOUTH)
                            vertexOrder[i] = i;
                        else if (side == EnumFacing.WEST)
                            vertexOrder[i] = (i + 1) % 4;
                        else
                            vertexOrder[i] = (i + 3) % 4;
                    }
                    vec.transform(NORTH_ROTATION);
                    break;
                case SOUTH:
                    if (side != null) {
                        if (side == EnumFacing.NORTH)
                            vertexOrder[i] = i;
                        else if (side == EnumFacing.SOUTH)
                            vertexOrder[i] = (i + 2) % 4;
                        else if (side == EnumFacing.WEST)
                            vertexOrder[i] = (i + 3) % 4;
                        else
                            vertexOrder[i] = (i + 1) % 4;
                    }
                    vec.transform(SOUTH_ROTATION);
                    vec.transform(ROTATE_Z_180);
                    break;
                case WEST:
                    if (side != null) {
                        if (side == EnumFacing.NORTH)
                            vertexOrder[i] = (i + 3) % 4;
                        else if (side == EnumFacing.SOUTH)
                            vertexOrder[i] = (i + 1) % 4;
                        else if (side == EnumFacing.EAST)
                            vertexOrder[i] = i % 4;
                        else
                            vertexOrder[i] = (i + 2) % 4;
                    }
                    vec.transform(WEST_ROTATION);
                    vec.transform(ROTATE_X_90);
                    break;
                case EAST:
                    if (side != null) {
                        if (side == EnumFacing.NORTH)
                            vertexOrder[i] = (i + 1) % 4;
                        else if (side == EnumFacing.SOUTH)
                            vertexOrder[i] = (i + 3) % 4;
                        else if (side == EnumFacing.EAST)
                            vertexOrder[i] = (i + 2) % 4;
                        else
                            vertexOrder[i] = i % 4;
                    }
                    vec.transform(EAST_ROTATION);
                    vec.transform(ROTATE_X_90);
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
//            data[i * 7] = vertexData[i * 7];
//            data[i * 7 + 1] = vertexData[i * 7 + 1];
//            data[i * 7 + 2] = vertexData[i * 7 + 2];
            data[i * 7 + 3] = vertexData[i * 7 + 3]; // shade colour
            data[i * 7 + 4] = vertexData[i * 7 + 4]; // texture U
            data[i * 7 + 5] = vertexData[i * 7 + 5]; // texture V
        }

        int[] finalData = new int[data.length];
        for (int i = 0; i < vertexOrder.length; i++) {
            int j = vertexOrder[i] * 7;
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

//    private EnumFacing getSideForDirection(Direction side, EnumFacing direction) {
//        if (side == null)
//            return null;
//        if (side.getAxis() == (direction.getAxis() == EnumFacing.Axis.X ? EnumFacing.Axis.Z : EnumFacing.Axis.X))
//            return side;
//        if (side == EnumFacing.UP)
//            return direction;
//        if (side == EnumFacing.DOWN)
//            return direction.getOpposite();
//        if (side.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
//            return EnumFacing.UP;
//        return EnumFacing.DOWN;
//    }

    private List<BakedQuad> getQuadsForState(@Nullable IBlockState state, @Nullable EnumFacing side, long rand, int tintOffset, @Nonnull EnumFacing direction, boolean positiveState, boolean rotate) {
        if (state == null) return new ArrayList<>();
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        if (!rotate)
            return new ArrayList<>(model.getQuads(state, side, rand));
        return new ArrayList<>(model.getQuads(state, Utils.rotateFace(side, direction), rand).stream().map(quad -> {
            int[] vertexData = rotateVertexData(quad.getVertexData(), direction, side, positiveState);
            return new BakedQuad(vertexData, quad.hasTintIndex() ? quad.getTintIndex() + tintOffset : -1, FaceBakery.getFacingFromVertexData(vertexData), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        }).collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null)
            return getFallback().getQuads(null, side, rand);
        IBlockState negativeState = ((IExtendedBlockState) state).getValue(BlockVerticalSlab.NEGATIVE);
        IBlockState positiveState = ((IExtendedBlockState) state).getValue(BlockVerticalSlab.POSITIVE);
        EnumFacing direction = state.getValue(BlockVerticalSlab.FACING);
        String cacheKey = (negativeState != null ? negativeState.toString() : "null") + "," + (positiveState != null ? positiveState.toString() : "null") +
                ":" + (side != null ? side.getName() : "null") + ":" +
                (MinecraftForgeClient.getRenderLayer() != null ? MinecraftForgeClient.getRenderLayer().toString() : "null") + "," + direction.getName();
        cache.clear();
        if (!cache.containsKey(cacheKey)) {
            boolean negativeTransparent = negativeState != null && Utils.isTransparent(negativeState);
            boolean positiveTransparent = positiveState != null && Utils.isTransparent(positiveState);

            List<BakedQuad> quads = new ArrayList<>();
            if (positiveState != null && MinecraftForgeClient.getRenderLayer() == positiveState.getBlock().getRenderLayer()) {
                List<BakedQuad> positiveQuads = getQuadsForState(positiveState, side, rand, 0, direction, true, ((IExtendedBlockState) state).getValue(BlockVerticalSlab.ROTATE_POSITIVE));
                if (negativeState != null && ((!negativeTransparent && !positiveTransparent) || (positiveTransparent && !negativeTransparent) || (positiveTransparent && negativeTransparent)))
                    positiveQuads.removeIf(bakedQuad -> bakedQuad.getFace() == direction.getOpposite());
                quads.addAll(positiveQuads);
            }
            if (negativeState != null && MinecraftForgeClient.getRenderLayer() == negativeState.getBlock().getRenderLayer()) {
                List<BakedQuad> negativeQuads = getQuadsForState(negativeState, side, rand, TINT_OFFSET, direction, false, ((IExtendedBlockState) state).getValue(BlockVerticalSlab.ROTATE_NEGATIVE));
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
}
