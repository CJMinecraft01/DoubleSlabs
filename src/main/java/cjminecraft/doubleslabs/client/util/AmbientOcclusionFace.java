package cjminecraft.doubleslabs.client.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.BitSet;

public class AmbientOcclusionFace {

    public final float[] vertexColorMultiplier = new float[4];
    public final int[] vertexBrightness = new int[4];

    public AmbientOcclusionFace() {
    }

    public void updateVertexBrightness(ILightReader worldIn, BlockState state, BlockPos centerPos, Direction directionIn, float[] faceShape, BitSet shapeState) {
        BlockPos blockpos = shapeState.get(0) ? centerPos.offset(directionIn) : centerPos;
        NeighborInfo blockmodelrenderer$neighborinfo = NeighborInfo.getNeighbourInfo(directionIn);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        BlockModelRenderer.Cache blockmodelrenderer$cache = BlockModelRenderer.CACHE_COMBINED_LIGHT.get();
        blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]);
        BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
        int i = blockmodelrenderer$cache.getPackedLight(blockstate, worldIn, blockpos$mutable);
        float f = blockmodelrenderer$cache.getBrightness(blockstate, worldIn, blockpos$mutable);
        blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]);
        BlockState blockstate1 = worldIn.getBlockState(blockpos$mutable);
        int j = blockmodelrenderer$cache.getPackedLight(blockstate1, worldIn, blockpos$mutable);
        float f1 = blockmodelrenderer$cache.getBrightness(blockstate1, worldIn, blockpos$mutable);
        blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[2]);
        BlockState blockstate2 = worldIn.getBlockState(blockpos$mutable);
        int k = blockmodelrenderer$cache.getPackedLight(blockstate2, worldIn, blockpos$mutable);
        float f2 = blockmodelrenderer$cache.getBrightness(blockstate2, worldIn, blockpos$mutable);
        blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[3]);
        BlockState blockstate3 = worldIn.getBlockState(blockpos$mutable);
        int l = blockmodelrenderer$cache.getPackedLight(blockstate3, worldIn, blockpos$mutable);
        float f3 = blockmodelrenderer$cache.getBrightness(blockstate3, worldIn, blockpos$mutable);
        blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(directionIn);
        boolean flag = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
        blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(directionIn);
        boolean flag1 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
        blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[2]).move(directionIn);
        boolean flag2 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
        blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[3]).move(directionIn);
        boolean flag3 = worldIn.getBlockState(blockpos$mutable).getOpacity(worldIn, blockpos$mutable) == 0;
        float f4;
        int i1;
        if (!flag2 && !flag) {
            f4 = f;
            i1 = i;
        } else {
            blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate4 = worldIn.getBlockState(blockpos$mutable);
            f4 = blockmodelrenderer$cache.getBrightness(blockstate4, worldIn, blockpos$mutable);
            i1 = blockmodelrenderer$cache.getPackedLight(blockstate4, worldIn, blockpos$mutable);
        }

        float f5;
        int j1;
        if (!flag3 && !flag) {
            f5 = f;
            j1 = i;
        } else {
            blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate6 = worldIn.getBlockState(blockpos$mutable);
            f5 = blockmodelrenderer$cache.getBrightness(blockstate6, worldIn, blockpos$mutable);
            j1 = blockmodelrenderer$cache.getPackedLight(blockstate6, worldIn, blockpos$mutable);
        }

        float f6;
        int k1;
        if (!flag2 && !flag1) {
            f6 = f;
            k1 = i;
        } else {
            blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate7 = worldIn.getBlockState(blockpos$mutable);
            f6 = blockmodelrenderer$cache.getBrightness(blockstate7, worldIn, blockpos$mutable);
            k1 = blockmodelrenderer$cache.getPackedLight(blockstate7, worldIn, blockpos$mutable);
        }

        float f7;
        int l1;
        if (!flag3 && !flag1) {
            f7 = f;
            l1 = i;
        } else {
            blockpos$mutable.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate8 = worldIn.getBlockState(blockpos$mutable);
            f7 = blockmodelrenderer$cache.getBrightness(blockstate8, worldIn, blockpos$mutable);
            l1 = blockmodelrenderer$cache.getPackedLight(blockstate8, worldIn, blockpos$mutable);
        }

        int i3 = blockmodelrenderer$cache.getPackedLight(state, worldIn, centerPos);
        blockpos$mutable.setPos(centerPos).move(directionIn);
        BlockState blockstate5 = worldIn.getBlockState(blockpos$mutable);
        if (shapeState.get(0) || !blockstate5.isOpaqueCube(worldIn, blockpos$mutable)) {
            i3 = blockmodelrenderer$cache.getPackedLight(blockstate5, worldIn, blockpos$mutable);
        }

        float f8 = shapeState.get(0) ? blockmodelrenderer$cache.getBrightness(worldIn.getBlockState(blockpos), worldIn, blockpos) : blockmodelrenderer$cache.getBrightness(worldIn.getBlockState(centerPos), worldIn, centerPos);
        VertexTranslations blockmodelrenderer$vertextranslations = VertexTranslations.getVertexTranslations(directionIn);
        if (shapeState.get(1) && blockmodelrenderer$neighborinfo.doNonCubicWeight) {
            float f29 = (f3 + f + f5 + f8) * 0.25F;
            float f30 = (f2 + f + f4 + f8) * 0.25F;
            float f31 = (f2 + f1 + f6 + f8) * 0.25F;
            float f32 = (f3 + f1 + f7 + f8) * 0.25F;
            float f13 = faceShape[blockmodelrenderer$neighborinfo.vert0Weights[0].shape] * faceShape[blockmodelrenderer$neighborinfo.vert0Weights[1].shape];
            float f14 = faceShape[blockmodelrenderer$neighborinfo.vert0Weights[2].shape] * faceShape[blockmodelrenderer$neighborinfo.vert0Weights[3].shape];
            float f15 = faceShape[blockmodelrenderer$neighborinfo.vert0Weights[4].shape] * faceShape[blockmodelrenderer$neighborinfo.vert0Weights[5].shape];
            float f16 = faceShape[blockmodelrenderer$neighborinfo.vert0Weights[6].shape] * faceShape[blockmodelrenderer$neighborinfo.vert0Weights[7].shape];
            float f17 = faceShape[blockmodelrenderer$neighborinfo.vert1Weights[0].shape] * faceShape[blockmodelrenderer$neighborinfo.vert1Weights[1].shape];
            float f18 = faceShape[blockmodelrenderer$neighborinfo.vert1Weights[2].shape] * faceShape[blockmodelrenderer$neighborinfo.vert1Weights[3].shape];
            float f19 = faceShape[blockmodelrenderer$neighborinfo.vert1Weights[4].shape] * faceShape[blockmodelrenderer$neighborinfo.vert1Weights[5].shape];
            float f20 = faceShape[blockmodelrenderer$neighborinfo.vert1Weights[6].shape] * faceShape[blockmodelrenderer$neighborinfo.vert1Weights[7].shape];
            float f21 = faceShape[blockmodelrenderer$neighborinfo.vert2Weights[0].shape] * faceShape[blockmodelrenderer$neighborinfo.vert2Weights[1].shape];
            float f22 = faceShape[blockmodelrenderer$neighborinfo.vert2Weights[2].shape] * faceShape[blockmodelrenderer$neighborinfo.vert2Weights[3].shape];
            float f23 = faceShape[blockmodelrenderer$neighborinfo.vert2Weights[4].shape] * faceShape[blockmodelrenderer$neighborinfo.vert2Weights[5].shape];
            float f24 = faceShape[blockmodelrenderer$neighborinfo.vert2Weights[6].shape] * faceShape[blockmodelrenderer$neighborinfo.vert2Weights[7].shape];
            float f25 = faceShape[blockmodelrenderer$neighborinfo.vert3Weights[0].shape] * faceShape[blockmodelrenderer$neighborinfo.vert3Weights[1].shape];
            float f26 = faceShape[blockmodelrenderer$neighborinfo.vert3Weights[2].shape] * faceShape[blockmodelrenderer$neighborinfo.vert3Weights[3].shape];
            float f27 = faceShape[blockmodelrenderer$neighborinfo.vert3Weights[4].shape] * faceShape[blockmodelrenderer$neighborinfo.vert3Weights[5].shape];
            float f28 = faceShape[blockmodelrenderer$neighborinfo.vert3Weights[6].shape] * faceShape[blockmodelrenderer$neighborinfo.vert3Weights[7].shape];
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f30 * f14 + f31 * f15 + f32 * f16;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f30 * f18 + f31 * f19 + f32 * f20;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f30 * f22 + f31 * f23 + f32 * f24;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f30 * f26 + f31 * f27 + f32 * f28;
            int i2 = this.getAoBrightness(l, i, j1, i3);
            int j2 = this.getAoBrightness(k, i, i1, i3);
            int k2 = this.getAoBrightness(k, j, k1, i3);
            int l2 = this.getAoBrightness(l, j, l1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getVertexBrightness(i2, j2, k2, l2, f13, f14, f15, f16);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getVertexBrightness(i2, j2, k2, l2, f17, f18, f19, f20);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getVertexBrightness(i2, j2, k2, l2, f21, f22, f23, f24);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getVertexBrightness(i2, j2, k2, l2, f25, f26, f27, f28);
        } else {
            float f9 = (f3 + f + f5 + f8) * 0.25F;
            float f10 = (f2 + f + f4 + f8) * 0.25F;
            float f11 = (f2 + f1 + f6 + f8) * 0.25F;
            float f12 = (f3 + f1 + f7 + f8) * 0.25F;
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getAoBrightness(l, i, j1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getAoBrightness(k, i, i1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getAoBrightness(k, j, k1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getAoBrightness(l, j, l1, i3);
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f9;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f10;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f11;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f12;
        }

    }

    /**
     * Get ambient occlusion brightness
     */
    private int getAoBrightness(int br1, int br2, int br3, int br4) {
        if (br1 == 0) {
            br1 = br4;
        }

        if (br2 == 0) {
            br2 = br4;
        }

        if (br3 == 0) {
            br3 = br4;
        }

        return br1 + br2 + br3 + br4 >> 2 & 16711935;
    }

    private int getVertexBrightness(int b1, int b2, int b3, int b4, float w1, float w2, float w3, float w4) {
        int i = (int)((float)(b1 >> 16 & 255) * w1 + (float)(b2 >> 16 & 255) * w2 + (float)(b3 >> 16 & 255) * w3 + (float)(b4 >> 16 & 255) * w4) & 255;
        int j = (int)((float)(b1 & 255) * w1 + (float)(b2 & 255) * w2 + (float)(b3 & 255) * w3 + (float)(b4 & 255) * w4) & 255;
        return i << 16 | j;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum NeighborInfo {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new Orientation[]{Orientation.FLIP_WEST, Orientation.SOUTH, Orientation.FLIP_WEST, Orientation.FLIP_SOUTH, Orientation.WEST, Orientation.FLIP_SOUTH, Orientation.WEST, Orientation.SOUTH}, new Orientation[]{Orientation.FLIP_WEST, Orientation.NORTH, Orientation.FLIP_WEST, Orientation.FLIP_NORTH, Orientation.WEST, Orientation.FLIP_NORTH, Orientation.WEST, Orientation.NORTH}, new Orientation[]{Orientation.FLIP_EAST, Orientation.NORTH, Orientation.FLIP_EAST, Orientation.FLIP_NORTH, Orientation.EAST, Orientation.FLIP_NORTH, Orientation.EAST, Orientation.NORTH}, new Orientation[]{Orientation.FLIP_EAST, Orientation.SOUTH, Orientation.FLIP_EAST, Orientation.FLIP_SOUTH, Orientation.EAST, Orientation.FLIP_SOUTH, Orientation.EAST, Orientation.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new Orientation[]{Orientation.EAST, Orientation.SOUTH, Orientation.EAST, Orientation.FLIP_SOUTH, Orientation.FLIP_EAST, Orientation.FLIP_SOUTH, Orientation.FLIP_EAST, Orientation.SOUTH}, new Orientation[]{Orientation.EAST, Orientation.NORTH, Orientation.EAST, Orientation.FLIP_NORTH, Orientation.FLIP_EAST, Orientation.FLIP_NORTH, Orientation.FLIP_EAST, Orientation.NORTH}, new Orientation[]{Orientation.WEST, Orientation.NORTH, Orientation.WEST, Orientation.FLIP_NORTH, Orientation.FLIP_WEST, Orientation.FLIP_NORTH, Orientation.FLIP_WEST, Orientation.NORTH}, new Orientation[]{Orientation.WEST, Orientation.SOUTH, Orientation.WEST, Orientation.FLIP_SOUTH, Orientation.FLIP_WEST, Orientation.FLIP_SOUTH, Orientation.FLIP_WEST, Orientation.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new Orientation[]{Orientation.UP, Orientation.FLIP_WEST, Orientation.UP, Orientation.WEST, Orientation.FLIP_UP, Orientation.WEST, Orientation.FLIP_UP, Orientation.FLIP_WEST}, new Orientation[]{Orientation.UP, Orientation.FLIP_EAST, Orientation.UP, Orientation.EAST, Orientation.FLIP_UP, Orientation.EAST, Orientation.FLIP_UP, Orientation.FLIP_EAST}, new Orientation[]{Orientation.DOWN, Orientation.FLIP_EAST, Orientation.DOWN, Orientation.EAST, Orientation.FLIP_DOWN, Orientation.EAST, Orientation.FLIP_DOWN, Orientation.FLIP_EAST}, new Orientation[]{Orientation.DOWN, Orientation.FLIP_WEST, Orientation.DOWN, Orientation.WEST, Orientation.FLIP_DOWN, Orientation.WEST, Orientation.FLIP_DOWN, Orientation.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new Orientation[]{Orientation.UP, Orientation.FLIP_WEST, Orientation.FLIP_UP, Orientation.FLIP_WEST, Orientation.FLIP_UP, Orientation.WEST, Orientation.UP, Orientation.WEST}, new Orientation[]{Orientation.DOWN, Orientation.FLIP_WEST, Orientation.FLIP_DOWN, Orientation.FLIP_WEST, Orientation.FLIP_DOWN, Orientation.WEST, Orientation.DOWN, Orientation.WEST}, new Orientation[]{Orientation.DOWN, Orientation.FLIP_EAST, Orientation.FLIP_DOWN, Orientation.FLIP_EAST, Orientation.FLIP_DOWN, Orientation.EAST, Orientation.DOWN, Orientation.EAST}, new Orientation[]{Orientation.UP, Orientation.FLIP_EAST, Orientation.FLIP_UP, Orientation.FLIP_EAST, Orientation.FLIP_UP, Orientation.EAST, Orientation.UP, Orientation.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new Orientation[]{Orientation.UP, Orientation.SOUTH, Orientation.UP, Orientation.FLIP_SOUTH, Orientation.FLIP_UP, Orientation.FLIP_SOUTH, Orientation.FLIP_UP, Orientation.SOUTH}, new Orientation[]{Orientation.UP, Orientation.NORTH, Orientation.UP, Orientation.FLIP_NORTH, Orientation.FLIP_UP, Orientation.FLIP_NORTH, Orientation.FLIP_UP, Orientation.NORTH}, new Orientation[]{Orientation.DOWN, Orientation.NORTH, Orientation.DOWN, Orientation.FLIP_NORTH, Orientation.FLIP_DOWN, Orientation.FLIP_NORTH, Orientation.FLIP_DOWN, Orientation.NORTH}, new Orientation[]{Orientation.DOWN, Orientation.SOUTH, Orientation.DOWN, Orientation.FLIP_SOUTH, Orientation.FLIP_DOWN, Orientation.FLIP_SOUTH, Orientation.FLIP_DOWN, Orientation.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new Orientation[]{Orientation.FLIP_DOWN, Orientation.SOUTH, Orientation.FLIP_DOWN, Orientation.FLIP_SOUTH, Orientation.DOWN, Orientation.FLIP_SOUTH, Orientation.DOWN, Orientation.SOUTH}, new Orientation[]{Orientation.FLIP_DOWN, Orientation.NORTH, Orientation.FLIP_DOWN, Orientation.FLIP_NORTH, Orientation.DOWN, Orientation.FLIP_NORTH, Orientation.DOWN, Orientation.NORTH}, new Orientation[]{Orientation.FLIP_UP, Orientation.NORTH, Orientation.FLIP_UP, Orientation.FLIP_NORTH, Orientation.UP, Orientation.FLIP_NORTH, Orientation.UP, Orientation.NORTH}, new Orientation[]{Orientation.FLIP_UP, Orientation.SOUTH, Orientation.FLIP_UP, Orientation.FLIP_SOUTH, Orientation.UP, Orientation.FLIP_SOUTH, Orientation.UP, Orientation.SOUTH});

        private final Direction[] corners;
        private final boolean doNonCubicWeight;
        private final Orientation[] vert0Weights;
        private final Orientation[] vert1Weights;
        private final Orientation[] vert2Weights;
        private final Orientation[] vert3Weights;
        private static final NeighborInfo[] VALUES = Util.make(new NeighborInfo[6], (p_209260_0_) -> {
            p_209260_0_[Direction.DOWN.getIndex()] = DOWN;
            p_209260_0_[Direction.UP.getIndex()] = UP;
            p_209260_0_[Direction.NORTH.getIndex()] = NORTH;
            p_209260_0_[Direction.SOUTH.getIndex()] = SOUTH;
            p_209260_0_[Direction.WEST.getIndex()] = WEST;
            p_209260_0_[Direction.EAST.getIndex()] = EAST;
        });

        private NeighborInfo(Direction[] cornersIn, float brightness, boolean doNonCubicWeightIn, Orientation[] vert0WeightsIn, Orientation[] vert1WeightsIn, Orientation[] vert2WeightsIn, Orientation[] vert3WeightsIn) {
            this.corners = cornersIn;
            this.doNonCubicWeight = doNonCubicWeightIn;
            this.vert0Weights = vert0WeightsIn;
            this.vert1Weights = vert1WeightsIn;
            this.vert2Weights = vert2WeightsIn;
            this.vert3Weights = vert3WeightsIn;
        }

        public static NeighborInfo getNeighbourInfo(Direction facing) {
            return VALUES[facing.getIndex()];
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Orientation {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);

        private final int shape;

        private Orientation(Direction facingIn, boolean flip) {
            this.shape = facingIn.getIndex() + (flip ? Direction.values().length : 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum VertexTranslations {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int vert0;
        private final int vert1;
        private final int vert2;
        private final int vert3;
        private static final VertexTranslations[] VALUES = Util.make(new VertexTranslations[6], (p_209261_0_) -> {
            p_209261_0_[Direction.DOWN.getIndex()] = DOWN;
            p_209261_0_[Direction.UP.getIndex()] = UP;
            p_209261_0_[Direction.NORTH.getIndex()] = NORTH;
            p_209261_0_[Direction.SOUTH.getIndex()] = SOUTH;
            p_209261_0_[Direction.WEST.getIndex()] = WEST;
            p_209261_0_[Direction.EAST.getIndex()] = EAST;
        });

        private VertexTranslations(int vert0In, int vert1In, int vert2In, int vert3In) {
            this.vert0 = vert0In;
            this.vert1 = vert1In;
            this.vert2 = vert2In;
            this.vert3 = vert3In;
        }

        public static VertexTranslations getVertexTranslations(Direction facingIn) {
            return VALUES[facingIn.getIndex()];
        }
    }
}
