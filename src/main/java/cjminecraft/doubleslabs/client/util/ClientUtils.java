package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.FaceDirection;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class ClientUtils {
    private static final Quaternion ROTATE_X_90 = Vector3f.XP.rotationDegrees(90);
    private static final Quaternion ROTATE_Z_180 = Vector3f.ZP.rotationDegrees(180);

    public static boolean isTransparent(BlockState state) {
        return !state.getMaterial().isOpaque() || !state.isSolid();
    }

    private static Class<?> OPTIFINE_CONFIG;
    private static Method OPTIFINE_IS_SHADERS_METHOD;

    public static void checkOptiFineInstalled() {
        try {
            OPTIFINE_CONFIG = Class.forName("net.optifine.Config");
            OPTIFINE_IS_SHADERS_METHOD = OPTIFINE_CONFIG.getMethod("isShaders");
            DoubleSlabs.LOGGER.info("Detected OptiFine is installed. Applying fixes");
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            OPTIFINE_CONFIG = null;
            OPTIFINE_IS_SHADERS_METHOD = null;
        }
    }

    public static boolean isOptiFineInstalled() {
        return OPTIFINE_CONFIG != null;
    }

    public static boolean areShadersEnabled() {
        try {
            return isOptiFineInstalled() && (boolean) OPTIFINE_IS_SHADERS_METHOD.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            return false;
        }
    }

    private static final HashMap<Pair<Direction, Direction>, Consumer<Vector4f>> DIRECTION_TO_TRANSFORMATION = new HashMap<>();
    private static final HashMap<Direction, Quaternion> DIRECTION_TO_ANGLE = new HashMap<>();

    static {
        DIRECTION_TO_ANGLE.put(Direction.NORTH, Vector3f.YP.rotationDegrees(0));
        DIRECTION_TO_ANGLE.put(Direction.SOUTH, Vector3f.YP.rotationDegrees(180));
        DIRECTION_TO_ANGLE.put(Direction.WEST, Vector3f.YP.rotationDegrees(90));
        DIRECTION_TO_ANGLE.put(Direction.EAST, Vector3f.YP.rotationDegrees(270));

        Arrays.stream(Direction.values()).filter(direction -> !direction.getAxis().isVertical()).forEach(direction -> {
            Quaternion angle = DIRECTION_TO_ANGLE.get(direction);
            DIRECTION_TO_TRANSFORMATION.put(Pair.of(direction, null), vector -> {
                vector.transform(ROTATE_X_90);
                vector.transform(angle);
            });
            Arrays.stream(Direction.values()).forEach(side -> {
                DIRECTION_TO_TRANSFORMATION.put(Pair.of(direction, side), vector -> {
                    vector.transform(ROTATE_X_90);
                    if (side == direction)
                        vector.transform(ROTATE_Z_180);
                    vector.transform(angle);
                });
            });
        });
    }

    public static Consumer<Vector4f> getVertexTransformation(Direction direction, @Nullable Direction side) {
        return DIRECTION_TO_TRANSFORMATION.getOrDefault(Pair.of(direction, side), vector -> {});
    }

    private static float[] getPositions(Vector3f pos1, Vector3f pos2) {
        float[] positions = new float[Direction.values().length];
        positions[FaceDirection.Constants.WEST_INDEX] = pos1.getX();
        positions[FaceDirection.Constants.DOWN_INDEX] = pos1.getY();
        positions[FaceDirection.Constants.NORTH_INDEX] = pos1.getZ();
        positions[FaceDirection.Constants.EAST_INDEX] = pos2.getX();
        positions[FaceDirection.Constants.UP_INDEX] = pos2.getY();
        positions[FaceDirection.Constants.SOUTH_INDEX] = pos2.getZ();
        return positions;
    }

    private static boolean approximatelyEqual(float a, float b) {
        // to account for annoying floating point precision
        if (a == b)
            return true;
        float diff = Math.abs(a - b);
        return diff < 1e-2;
    }

    public static int[] rotateVertexData(int[] vertexData, Direction direction, @Nullable Direction side) {
        int[] data = new int[vertexData.length];
        
        float minX = 1.0f, minY = 1.0f, minZ = 1.0f, maxX = 0.0f, maxY = 0.0f, maxZ = 0.0f;

        // TODO replace 8 with the vertex format size
        for (int i = 0; i < vertexData.length / 8; i++) {
            // The x y z position relative to the center of the model
            float x = Float.intBitsToFloat(vertexData[i * 8]) - 0.5f;
            float y = Float.intBitsToFloat(vertexData[i * 8 + 1]) - 0.5f;
            float z = Float.intBitsToFloat(vertexData[i * 8 + 2]) - 0.5f;

            Vector4f vertex = new Vector4f(x, y, z, 0.0f);
            getVertexTransformation(direction, side).accept(vertex);

            float transformedX = vertex.getX() + 0.5f;
            float transformedY = vertex.getY() + 0.5f;
            float transformedZ = vertex.getZ() + 0.5f;
            
            if (transformedX < minX)
                minX = transformedX;
            else if (transformedX > maxX)
                maxX = transformedX;
            if (transformedY < minY)
                minY = transformedY;
            else if (transformedY > maxY)
                maxY = transformedY;
            if (transformedZ < minZ)
                minZ = transformedZ;
            else if (transformedZ > maxZ)
                maxZ = transformedZ;

            data[i * 8] = Float.floatToIntBits(transformedX);
            data[i * 8 + 1] = Float.floatToIntBits(transformedY);
            data[i * 8 + 2] = Float.floatToIntBits(transformedZ);
            data[i * 8 + 3] = vertexData[i * 8 + 3]; // shade colour
            data[i * 8 + 4] = vertexData[i * 8 + 4]; // texture U
            data[i * 8 + 5] = vertexData[i * 8 + 5]; // texture V
            data[i * 8 + 6] = vertexData[i * 8 + 6]; // baked lighting
            data[i * 8 + 7] = vertexData[i * 8 + 7]; // normal
        }

        if (side != null) {
            int[] finalData = new int[data.length];
            Vector3f from = new Vector3f(minX, minY, minZ);
            Vector3f to = new Vector3f(maxX, maxY, maxZ);
            float[] positions = getPositions(from, to);

            for (int i = 0; i < 4; i++) {
                FaceDirection faceDirection = FaceDirection.getFacing(side);
                FaceDirection.VertexInformation vertexInformation = faceDirection.getVertexInformation(i);
                finalData[i * 8] = Float.floatToRawIntBits(positions[vertexInformation.xIndex]);
                finalData[i * 8 + 1] = Float.floatToRawIntBits(positions[vertexInformation.yIndex]);
                finalData[i * 8 + 2] = Float.floatToRawIntBits(positions[vertexInformation.zIndex]);
                int newIndex = -1;
                for (int j = 0; j < 4; j++)
                    if (approximatelyEqual(Float.intBitsToFloat(data[j * 8]), positions[vertexInformation.xIndex]) && approximatelyEqual(Float.intBitsToFloat(data[j * 8 + 1]), positions[vertexInformation.yIndex]) && approximatelyEqual(Float.intBitsToFloat(data[j * 8 + 2]), positions[vertexInformation.zIndex]))
                        newIndex = j;
                if (newIndex < 0) {
                    finalData[i * 8 + 3] = data[i * 8 + 3];
                    finalData[i * 8 + 4] = data[i * 8 + 4];
                    finalData[i * 8 + 5] = data[i * 8 + 5];
                    finalData[i * 8 + 6] = data[i * 8 + 6];
                    finalData[i * 8 + 7] = data[i * 8 + 7];
                } else {
                    finalData[i * 8 + 3] = data[newIndex * 8 + 3];
                    finalData[i * 8 + 4] = data[newIndex * 8 + 4];
                    finalData[i * 8 + 5] = data[newIndex * 8 + 5];
                    finalData[i * 8 + 6] = data[newIndex * 8 + 6];
                    finalData[i * 8 + 7] = data[newIndex * 8 + 7];
                }
            }
            ForgeHooksClient.fillNormal(finalData, FaceBakery.getFacingFromVertexData(finalData));
            return finalData;
        }

        ForgeHooksClient.fillNormal(data, FaceBakery.getFacingFromVertexData(data));

        return data;
    }

    public static int[] changeQuadUVs(int[] vertexData, TextureAtlasSprite originalSprite, TextureAtlasSprite newSprite) {
        int[] data = new int[vertexData.length];
        for (int i = 0; i < vertexData.length / 8; i++) {
            data[i * 8] = vertexData[i * 8]; // x
            data[i * 8 + 1] = vertexData[i * 8 + 1]; // y
            data[i * 8 + 2] = vertexData[i * 8 + 2]; // z
            data[i * 8 + 3] = vertexData[i * 8 + 3]; // shade colour
            data[i * 8 + 4] = Float.floatToIntBits(Float.intBitsToFloat(vertexData[i * 8 + 4]) - originalSprite.getMinU() + newSprite.getMinU()); // texture U
            data[i * 8 + 5] = Float.floatToIntBits(Float.intBitsToFloat(vertexData[i * 8 + 5]) - originalSprite.getMinV() + newSprite.getMinV()); // texture V
            data[i * 8 + 6] = vertexData[i * 8 + 6]; // baked lighting
            data[i * 8 + 7] = vertexData[i * 8 + 7]; // normal
        }
        return data;
    }

    public static Direction rotateFace(Quaternion quaternion, Direction directionIn) {
        Vector3i vector3i = directionIn.getDirectionVec();
        Vector4f vector4f = new Vector4f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ(), 0.0F);
        vector4f.transform(quaternion);
        return Direction.getFacingFromVector(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }

    public static Direction rotateFace(Direction face, Direction verticalSlabDirection) {
        if (face == null)
            return null;
        Quaternion rotation = DIRECTION_TO_ANGLE.get(verticalSlabDirection);
        boolean negativeDirection = verticalSlabDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        boolean zAxis = verticalSlabDirection.getAxis() == Direction.Axis.Z;
        if (face == verticalSlabDirection)
            return Direction.DOWN;
        else if (face == verticalSlabDirection.getOpposite())
            return Direction.UP;
        else if (face == Direction.DOWN)
            return Direction.SOUTH;
        else if (face == Direction.UP)
            return Direction.NORTH;
        else if (face.getAxis() == Direction.Axis.X && zAxis)
            return negativeDirection ? face : face.getOpposite();
        else if (face.getAxis() == Direction.Axis.Z && !zAxis)
            return rotateFace(rotation, face).getOpposite();
        return face;
    }

    public static int[] offsetY(int[] vertexData, float amount) {
        int[] data = new int[vertexData.length];
        for (int i = 0; i < vertexData.length / 8; i++) {
            data[i * 8] = vertexData[i * 8];
            data[i * 8 + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(vertexData[i * 8 + 1]) + amount);
            data[i * 8 + 2] = vertexData[i * 8 + 2];
            data[i * 8 + 3] = vertexData[i * 8 + 3]; // shade colour
            data[i * 8 + 4] = vertexData[i * 8 + 4]; // texture U
            data[i * 8 + 5] = vertexData[i * 8 + 5]; // texture V
            data[i * 8 + 6] = vertexData[i * 8 + 6]; // baked lighting
            data[i * 8 + 7] = vertexData[i * 8 + 7]; // normal
        }
        return data;
    }

}
