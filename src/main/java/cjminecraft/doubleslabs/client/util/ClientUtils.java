package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.util.Quaternion;
import cjminecraft.doubleslabs.common.util.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Quat4f;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class ClientUtils {
    private static final Quaternion ROTATE_X_90 = new Quaternion(new Vector3f(1, 0, 0), 90, true);
    private static final Quaternion ROTATE_Z_180 = new Quaternion(new Vector3f(0, 0, 1), 180, true);

    public static boolean isTransparent(IBlockState state) {
        return !state.getMaterial().isOpaque() || state.isTranslucent();
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

    public static Quat4f convert(Quaternion quaternion) {
        return new Quat4f(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getW());
    }

    private static final HashMap<Pair<EnumFacing, EnumFacing>, Consumer<Vector3f>> DIRECTION_TO_TRANSFORMATION = new HashMap<>();
    private static final HashMap<EnumFacing, Quaternion> DIRECTION_TO_ANGLE = new HashMap<>();

    static {
        DIRECTION_TO_ANGLE.put(EnumFacing.NORTH, new Quaternion(new Vector3f(0, 1, 0), 0, true));
        DIRECTION_TO_ANGLE.put(EnumFacing.SOUTH, new Quaternion(new Vector3f(0, 1, 0), 180, true));
        DIRECTION_TO_ANGLE.put(EnumFacing.WEST, new Quaternion(new Vector3f(0, 1, 0), 90, true));
        DIRECTION_TO_ANGLE.put(EnumFacing.EAST, new Quaternion(new Vector3f(0, 1, 0), 270, true));

        Arrays.stream(EnumFacing.values()).filter(direction -> !direction.getAxis().isVertical()).forEach(direction -> {
            Quaternion angle = DIRECTION_TO_ANGLE.get(direction);
            DIRECTION_TO_TRANSFORMATION.put(Pair.of(direction, null), vector -> {
                vector.transform(ROTATE_X_90);
                vector.transform(angle);
            });
            Arrays.stream(EnumFacing.values()).forEach(side -> {
                DIRECTION_TO_TRANSFORMATION.put(Pair.of(direction, side), vector -> {
                    vector.transform(ROTATE_X_90);
                    if (side == direction)
                        vector.transform(ROTATE_Z_180);
                    vector.transform(angle);
                });
            });
        });
    }

    public static Consumer<Vector3f> getVertexTransformation(EnumFacing direction, @Nullable EnumFacing side) {
        return DIRECTION_TO_TRANSFORMATION.getOrDefault(Pair.of(direction, side), vector -> {});
    }

    private static float[] getPositions(Vector3f pos1, Vector3f pos2) {
        float[] positions = new float[EnumFacing.values().length];
        positions[EnumFaceDirection.Constants.WEST_INDEX] = pos1.getX();
        positions[EnumFaceDirection.Constants.DOWN_INDEX] = pos1.getY();
        positions[EnumFaceDirection.Constants.NORTH_INDEX] = pos1.getZ();
        positions[EnumFaceDirection.Constants.EAST_INDEX] = pos2.getX();
        positions[EnumFaceDirection.Constants.UP_INDEX] = pos2.getY();
        positions[EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.getZ();
        return positions;
    }

    private static boolean approximatelyEqual(float a, float b) {
        // to account for annoying floating point precision
        if (a == b)
            return true;
        float diff = Math.abs(a - b);
        return diff < 1e-2;
    }

    public static int[] rotateVertexData(int[] vertexData, EnumFacing direction, @Nullable EnumFacing side) {
        int[] data = new int[vertexData.length];
        
        float minX = 1.0f, minY = 1.0f, minZ = 1.0f, maxX = 0.0f, maxY = 0.0f, maxZ = 0.0f;

        // TODO replace 7 with the vertex format size
        for (int i = 0; i < vertexData.length / 7; i++) {
            // The x y z position relative to the center of the model
            float x = Float.intBitsToFloat(vertexData[i * 7]) - 0.5f;
            float y = Float.intBitsToFloat(vertexData[i * 7 + 1]) - 0.5f;
            float z = Float.intBitsToFloat(vertexData[i * 7 + 2]) - 0.5f;

            Vector3f vertex = new Vector3f(x, y, z);
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

            data[i * 7] = Float.floatToIntBits(transformedX);
            data[i * 7 + 1] = Float.floatToIntBits(transformedY);
            data[i * 7 + 2] = Float.floatToIntBits(transformedZ);
            data[i * 7 + 3] = vertexData[i * 7 + 3]; // shade colour
            data[i * 7 + 4] = vertexData[i * 7 + 4]; // texture U
            data[i * 7 + 5] = vertexData[i * 7 + 5]; // texture V
            data[i * 7 + 6] = vertexData[i * 7 + 6]; // baked lighting
        }

        if (side != null) {
            int[] finalData = new int[data.length];
            Vector3f from = new Vector3f(minX, minY, minZ);
            Vector3f to = new Vector3f(maxX, maxY, maxZ);
            float[] positions = getPositions(from, to);

            for (int i = 0; i < 4; i++) {
                EnumFaceDirection faceDirection = EnumFaceDirection.getFacing(side);
                EnumFaceDirection.VertexInformation vertexInformation = faceDirection.getVertexInformation(i);
                finalData[i * 7] = Float.floatToRawIntBits(positions[vertexInformation.xIndex]);
                finalData[i * 7 + 1] = Float.floatToRawIntBits(positions[vertexInformation.yIndex]);
                finalData[i * 7 + 2] = Float.floatToRawIntBits(positions[vertexInformation.zIndex]);
                int newIndex = -1;
                for (int j = 0; j < 4; j++) {
                    if (approximatelyEqual(Float.intBitsToFloat(data[j * 7]), positions[vertexInformation.xIndex]) && approximatelyEqual(Float.intBitsToFloat(data[j * 7 + 1]), positions[vertexInformation.yIndex]) && approximatelyEqual(Float.intBitsToFloat(data[j * 7 + 2]), positions[vertexInformation.zIndex])) {
                        newIndex = j;
                        break;
                    }
                }
                if (newIndex < 0) {
                    finalData[i * 7 + 3] = data[i * 7 + 3];
                    finalData[i * 7 + 4] = data[i * 7 + 4];
                    finalData[i * 7 + 5] = data[i * 7 + 5];
                    finalData[i * 7 + 6] = data[i * 7 + 6];
                } else {
                    finalData[i * 7 + 3] = data[newIndex * 7 + 3];
                    finalData[i * 7 + 4] = data[newIndex * 7 + 4];
                    finalData[i * 7 + 5] = data[newIndex * 7 + 5];
                    finalData[i * 7 + 6] = data[newIndex * 7 + 6];
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
        for (int i = 0; i < vertexData.length / 7; i++) {
            data[i * 7] = vertexData[i * 7]; // x
            data[i * 7 + 1] = vertexData[i * 7 + 1]; // y
            data[i * 7 + 2] = vertexData[i * 7 + 2]; // z
            data[i * 7 + 3] = vertexData[i * 7 + 3]; // shade colour
            data[i * 7 + 4] = Float.floatToIntBits(Float.intBitsToFloat(vertexData[i * 7 + 4]) - originalSprite.getMinU() + newSprite.getMinU()); // texture U
            data[i * 7 + 5] = Float.floatToIntBits(Float.intBitsToFloat(vertexData[i * 7 + 5]) - originalSprite.getMinV() + newSprite.getMinV()); // texture V
            data[i * 7 + 6] = vertexData[i * 7 + 6]; // baked lighting
        }
        return data;
    }

    public static EnumFacing rotateFace(Quaternion quaternion, EnumFacing directionIn) {
        Vec3i vector3i = directionIn.getDirectionVec();
        Vector3f vector3f = new Vector3f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ());
        vector3f.transform(quaternion);
        return EnumFacing.getFacingFromVector(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    public static EnumFacing rotateFace(EnumFacing face, EnumFacing verticalSlabDirection) {
        if (face == null)
            return null;
        Quaternion rotation = DIRECTION_TO_ANGLE.get(verticalSlabDirection);
        boolean negativeDirection = verticalSlabDirection.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
        boolean zAxis = verticalSlabDirection.getAxis() == EnumFacing.Axis.Z;
        if (face == verticalSlabDirection)
            return EnumFacing.DOWN;
        else if (face == verticalSlabDirection.getOpposite())
            return EnumFacing.UP;
        else if (face == EnumFacing.DOWN)
            return EnumFacing.SOUTH;
        else if (face == EnumFacing.UP)
            return EnumFacing.NORTH;
        else if (face.getAxis() == EnumFacing.Axis.X && zAxis)
            return negativeDirection ? face : face.getOpposite();
        else if (face.getAxis() == EnumFacing.Axis.Z && !zAxis)
            return rotateFace(rotation, face).getOpposite();
        return face;
    }

    public static int[] offsetY(int[] vertexData, float amount) {
        int[] data = new int[vertexData.length];
        for (int i = 0; i < vertexData.length / 7; i++) {
            data[i * 7] = vertexData[i * 7];
            data[i * 7 + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(vertexData[i * 7 + 1]) + amount);
            data[i * 7 + 2] = vertexData[i * 7 + 2];
            data[i * 7 + 3] = vertexData[i * 7 + 3]; // shade colour
            data[i * 7 + 4] = vertexData[i * 7 + 4]; // texture U
            data[i * 7 + 5] = vertexData[i * 7 + 5]; // texture V
            data[i * 7 + 6] = vertexData[i * 7 + 6]; // baked lighting
        }
        return data;
    }

}
