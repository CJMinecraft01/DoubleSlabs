package cjminecraft.doubleslabs.client.util.vertex;

import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.common.util.Vector3f;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

// Based off QuadTransformer
public class VerticalSlabTransformer {

    private static int POSITION = findPositionOffset(DefaultVertexFormats.BLOCK);
    private static int NORMAL = findNormalOffset(DefaultVertexFormats.BLOCK);
    private final EnumFacing verticalSlabDirection;
    @Nullable
    private final EnumFacing side;
    private final boolean positive;
    private final Consumer<Vector3f> transformation;

    public VerticalSlabTransformer(EnumFacing verticalSlabDirection, @Nullable EnumFacing side, boolean positive) {
        this.verticalSlabDirection = verticalSlabDirection;
        this.side = side;
        this.positive = positive;
        this.transformation = ClientUtils.getVertexTransformation(this.verticalSlabDirection, this.side);
    }

    public static void reload() {
        POSITION = findPositionOffset(DefaultVertexFormats.BLOCK);
        NORMAL = findNormalOffset(DefaultVertexFormats.BLOCK);
    }

    private static int getAtByteOffset(int[] inData, int offset) {
        int index = offset / 4;
        int lsb = inData[index];

        int shift = (offset % 4) * 8;
        if (shift == 0)
            return inData[index];

        int msb = inData[index + 1];

        return (lsb >>> shift) | (msb << (32 - shift));
    }

    private static void putAtByteOffset(int[] outData, int offset, int value) {
        int index = offset / 4;
        int shift = (offset % 4) * 8;

        if (shift == 0) {
            outData[index] = value;
            return;
        }

        int lsbMask = 0xFFFFFFFF >>> (32 - shift);
        int msbMask = 0xFFFFFFFF << shift;

        outData[index] = (outData[index] & lsbMask) | (value << shift);
        outData[index + 1] = (outData[index + 1] & msbMask) | (value >>> (32 - shift));
    }

    private static int findPositionOffset(VertexFormat fmt) {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.getElements().size(); index++) {
            VertexFormatElement el = fmt.getElements().get(index);
            if (el.getUsage() == VertexFormatElement.EnumUsage.POSITION) {
                element = el;
                break;
            }
        }
        if (index == fmt.getElements().size() || element == null)
            throw new RuntimeException("Expected vertex format to have a POSITION attribute");
        if (element.getType() != VertexFormatElement.EnumType.FLOAT)
            throw new RuntimeException("Expected POSITION attribute to have data type FLOAT");
        if (element.getSize() < 3)
            throw new RuntimeException("Expected POSITION attribute to have at least 3 dimensions");
        return fmt.getOffset(index);
    }

    private static int findNormalOffset(VertexFormat fmt) {
        int index;
        VertexFormatElement element = null;
        for (index = 0; index < fmt.getElementCount(); index++) {
            VertexFormatElement el = fmt.getElement(index);
            if (el.getUsage() == VertexFormatElement.EnumUsage.NORMAL) {
                element = el;
                break;
            }
        }
        if (index == fmt.getElementCount() || element == null)
            return -1;
        if (element.getType() != VertexFormatElement.EnumType.BYTE)
            throw new RuntimeException("Expected NORMAL attribute to have data type BYTE");
        if (element.getSize() < 3)
            throw new RuntimeException("Expected NORMAL attribute to have at least 3 dimensions");
        return fmt.getOffset(index);
    }

    private void processVertices(int[] inData, int[] outData) {
        int stride = DefaultVertexFormats.BLOCK.getSize();
        int count = (inData.length * 4) / stride;
        for (int i = 0; i < count; i++) {
            int offset = POSITION + i * stride;
            float x = Float.intBitsToFloat(getAtByteOffset(inData, offset)) - 0.5f;
            float y = Float.intBitsToFloat(getAtByteOffset(inData, offset + 4)) - 0.5f;
            float z = Float.intBitsToFloat(getAtByteOffset(inData, offset + 8)) - 0.5f;

            Vector3f pos = new Vector3f(x, y, z);
            this.transformation.accept(pos);
//            if (this.side == this.verticalSlabDirection)
//                pos.transform(new Vector3f(Vector3d.copyCentered(this.verticalSlabDirection.getDirectionVec())).rotationDegrees(180));
//            pos.perspectiveDivide();

            putAtByteOffset(outData, offset, Float.floatToRawIntBits(pos.getX() + 0.5f));
            putAtByteOffset(outData, offset + 4, Float.floatToRawIntBits(pos.getY() + 0.5f));
            putAtByteOffset(outData, offset + 8, Float.floatToRawIntBits(pos.getZ() + 0.5f));
        }

        for (int i = 0; i < count; i++) {
            int offset = NORMAL + i * stride;
            int normalIn = getAtByteOffset(inData, offset);
            if (normalIn != 0) {

                float x = ((byte) ((normalIn) >> 24)) / 127.0f - 0.5f;
                float y = ((byte) ((normalIn << 8) >> 24)) / 127.0f - 0.5f;
                float z = ((byte) ((normalIn << 16) >> 24)) / 127.0f - 0.5f;

                Vector3f pos = new Vector3f(x, y, z);
                ClientUtils.getVertexTransformation(verticalSlabDirection, null).accept(pos);
//                this.transformation.accept(pos);
                pos.set(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
                pos.normalize();

                int normalOut = ((((byte) (x / 127.0f)) & 0xFF) << 24) |
                        ((((byte) (y / 127.0f)) & 0xFF) << 16) |
                        ((((byte) (z / 127.0f)) & 0xFF) << 8) |
                        (normalIn & 0xFF);

                putAtByteOffset(outData, offset, normalOut);
            }
        }
    }

    private int getTintIndex(BakedQuad quad) {
        return quad.hasTintIndex() ? this.positive ? quad.getTintIndex() + ClientConstants.TINT_OFFSET : quad.getTintIndex() : -1;
    }

    /**
     * Processes a single quad, producing a new quad.
     *
     * @param input A single quad to transform.
     * @return A new BakedQuad object with the new position.
     */
    public BakedQuad processOne(BakedQuad input) {
        int[] inData = input.getVertexData();
        int[] outData = Arrays.copyOf(inData, inData.length);
        processVertices(inData, outData);

        return new BakedQuad(outData, getTintIndex(input), FaceBakery.getFacingFromVertexData(outData), input.getSprite(), input.shouldApplyDiffuseLighting(), input.getFormat());
    }

    /**
     * Processes multiple quads, producing a new array of new quads.
     *
     * @param inputs The list of quads to transform
     * @return A new array of new BakedQuad objects.
     */
    public List<BakedQuad> processMany(List<BakedQuad> inputs) {
        if (inputs.size() == 0)
            return Collections.emptyList();

        List<BakedQuad> outputs = Lists.newArrayList();
        for (BakedQuad input : inputs) {
            int[] inData = input.getVertexData();
            int[] outData = Arrays.copyOf(inData, inData.length);
            processVertices(inData, outData);

            outputs.add(new BakedQuad(outData, getTintIndex(input), FaceBakery.getFacingFromVertexData(outData), input.getSprite(), input.shouldApplyDiffuseLighting(), input.getFormat()));
        }
        return outputs;
    }

}
