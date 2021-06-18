package cjminecraft.doubleslabs.client.util;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.BitSet;

public class AmbientOcclusionFace {

    public final float[] vertexColorMultiplier = new float[4];
    public final int[] vertexBrightness = new int[4];

    public void func_239285_a_(IBlockDisplayReader p_239285_1_, BlockState p_239285_2_, BlockPos p_239285_3_, Direction p_239285_4_, float[] p_239285_5_, BitSet p_239285_6_, boolean p_239285_7_) {
        BlockPos blockpos = p_239285_6_.get(0) ? p_239285_3_.offset(p_239285_4_) : p_239285_3_;
        NeighborInfo blockmodelrenderer$neighborinfo = NeighborInfo.getNeighbourInfo(p_239285_4_);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        BlockModelRenderer.Cache blockmodelrenderer$cache = BlockModelRenderer.CACHE_COMBINED_LIGHT.get();
        blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[0]);
        BlockState blockstate = p_239285_1_.getBlockState(blockpos$mutable);
        int i = blockmodelrenderer$cache.getPackedLight(blockstate, p_239285_1_, blockpos$mutable);
        float f = blockmodelrenderer$cache.getBrightness(blockstate, p_239285_1_, blockpos$mutable);
        blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[1]);
        BlockState blockstate1 = p_239285_1_.getBlockState(blockpos$mutable);
        int j = blockmodelrenderer$cache.getPackedLight(blockstate1, p_239285_1_, blockpos$mutable);
        float f1 = blockmodelrenderer$cache.getBrightness(blockstate1, p_239285_1_, blockpos$mutable);
        blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[2]);
        BlockState blockstate2 = p_239285_1_.getBlockState(blockpos$mutable);
        int k = blockmodelrenderer$cache.getPackedLight(blockstate2, p_239285_1_, blockpos$mutable);
        float f2 = blockmodelrenderer$cache.getBrightness(blockstate2, p_239285_1_, blockpos$mutable);
        blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[3]);
        BlockState blockstate3 = p_239285_1_.getBlockState(blockpos$mutable);
        int l = blockmodelrenderer$cache.getPackedLight(blockstate3, p_239285_1_, blockpos$mutable);
        float f3 = blockmodelrenderer$cache.getBrightness(blockstate3, p_239285_1_, blockpos$mutable);
        blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[0]).move(p_239285_4_);
        boolean flag = p_239285_1_.getBlockState(blockpos$mutable).getOpacity(p_239285_1_, blockpos$mutable) == 0;
        blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[1]).move(p_239285_4_);
        boolean flag1 = p_239285_1_.getBlockState(blockpos$mutable).getOpacity(p_239285_1_, blockpos$mutable) == 0;
        blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[2]).move(p_239285_4_);
        boolean flag2 = p_239285_1_.getBlockState(blockpos$mutable).getOpacity(p_239285_1_, blockpos$mutable) == 0;
        blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[3]).move(p_239285_4_);
        boolean flag3 = p_239285_1_.getBlockState(blockpos$mutable).getOpacity(p_239285_1_, blockpos$mutable) == 0;
        float f4;
        int i1;
        if (!flag2 && !flag) {
            f4 = f;
            i1 = i;
        } else {
            blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate4 = p_239285_1_.getBlockState(blockpos$mutable);
            f4 = blockmodelrenderer$cache.getBrightness(blockstate4, p_239285_1_, blockpos$mutable);
            i1 = blockmodelrenderer$cache.getPackedLight(blockstate4, p_239285_1_, blockpos$mutable);
        }

        float f5;
        int j1;
        if (!flag3 && !flag) {
            f5 = f;
            j1 = i;
        } else {
            blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate6 = p_239285_1_.getBlockState(blockpos$mutable);
            f5 = blockmodelrenderer$cache.getBrightness(blockstate6, p_239285_1_, blockpos$mutable);
            j1 = blockmodelrenderer$cache.getPackedLight(blockstate6, p_239285_1_, blockpos$mutable);
        }

        float f6;
        int k1;
        if (!flag2 && !flag1) {
            f6 = f;
            k1 = i;
        } else {
            blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate7 = p_239285_1_.getBlockState(blockpos$mutable);
            f6 = blockmodelrenderer$cache.getBrightness(blockstate7, p_239285_1_, blockpos$mutable);
            k1 = blockmodelrenderer$cache.getPackedLight(blockstate7, p_239285_1_, blockpos$mutable);
        }

        float f7;
        int l1;
        if (!flag3 && !flag1) {
            f7 = f;
            l1 = i;
        } else {
            blockpos$mutable.setAndMove(blockpos, blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate8 = p_239285_1_.getBlockState(blockpos$mutable);
            f7 = blockmodelrenderer$cache.getBrightness(blockstate8, p_239285_1_, blockpos$mutable);
            l1 = blockmodelrenderer$cache.getPackedLight(blockstate8, p_239285_1_, blockpos$mutable);
        }

        int i3 = blockmodelrenderer$cache.getPackedLight(p_239285_2_, p_239285_1_, p_239285_3_);
        blockpos$mutable.setAndMove(p_239285_3_, p_239285_4_);
        BlockState blockstate5 = p_239285_1_.getBlockState(blockpos$mutable);
        if (p_239285_6_.get(0) || !blockstate5.isOpaqueCube(p_239285_1_, blockpos$mutable)) {
            i3 = blockmodelrenderer$cache.getPackedLight(blockstate5, p_239285_1_, blockpos$mutable);
        }

        float f8 = p_239285_6_.get(0) ? blockmodelrenderer$cache.getBrightness(p_239285_1_.getBlockState(blockpos), p_239285_1_, blockpos) : blockmodelrenderer$cache.getBrightness(p_239285_1_.getBlockState(p_239285_3_), p_239285_1_, p_239285_3_);
        VertexTranslations blockmodelrenderer$vertextranslations = VertexTranslations.getVertexTranslations(p_239285_4_);
        if (p_239285_6_.get(1) && blockmodelrenderer$neighborinfo.doNonCubicWeight) {
            float f29 = (f3 + f + f5 + f8) * 0.25F;
            float f31 = (f2 + f + f4 + f8) * 0.25F;
            float f32 = (f2 + f1 + f6 + f8) * 0.25F;
            float f33 = (f3 + f1 + f7 + f8) * 0.25F;
            float f13 = p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[0].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[1].shape];
            float f14 = p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[2].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[3].shape];
            float f15 = p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[4].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[5].shape];
            float f16 = p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[6].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert0Weights[7].shape];
            float f17 = p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[0].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[1].shape];
            float f18 = p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[2].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[3].shape];
            float f19 = p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[4].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[5].shape];
            float f20 = p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[6].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert1Weights[7].shape];
            float f21 = p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[0].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[1].shape];
            float f22 = p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[2].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[3].shape];
            float f23 = p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[4].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[5].shape];
            float f24 = p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[6].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert2Weights[7].shape];
            float f25 = p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[0].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[1].shape];
            float f26 = p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[2].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[3].shape];
            float f27 = p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[4].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[5].shape];
            float f28 = p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[6].shape] * p_239285_5_[blockmodelrenderer$neighborinfo.vert3Weights[7].shape];
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f31 * f14 + f32 * f15 + f33 * f16;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f31 * f18 + f32 * f19 + f33 * f20;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f31 * f22 + f32 * f23 + f33 * f24;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f31 * f26 + f32 * f27 + f33 * f28;
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

        float f30 = p_239285_1_.func_230487_a_(p_239285_4_, p_239285_7_);

        for (int j3 = 0; j3 < this.vertexColorMultiplier.length; ++j3) {
            this.vertexColorMultiplier[j3] *= f30;
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
        int i = (int) ((float) (b1 >> 16 & 255) * w1 + (float) (b2 >> 16 & 255) * w2 + (float) (b3 >> 16 & 255) * w3 + (float) (b4 >> 16 & 255) * w4) & 255;
        int j = (int) ((float) (b1 & 255) * w1 + (float) (b2 & 255) * w2 + (float) (b3 & 255) * w3 + (float) (b4 & 255) * w4) & 255;
        return i << 16 | j;
    }

    @OnlyIn(Dist.CLIENT)
    static class Cache {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap packedLightCache = Util.make(() -> {
            Long2IntLinkedOpenHashMap long2intlinkedopenhashmap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
                protected void rehash(int p_rehash_1_) {
                }
            };
            long2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
            return long2intlinkedopenhashmap;
        });
        private final Long2FloatLinkedOpenHashMap brightnessCache = Util.make(() -> {
            Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
                protected void rehash(int p_rehash_1_) {
                }
            };
            long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
            return long2floatlinkedopenhashmap;
        });

        private Cache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.packedLightCache.clear();
            this.brightnessCache.clear();
        }

        public int getPackedLight(BlockState blockStateIn, IBlockDisplayReader lightReaderIn, BlockPos blockPosIn) {
            long i = blockPosIn.toLong();
            if (this.enabled) {
                int j = this.packedLightCache.get(i);
                if (j != Integer.MAX_VALUE) {
                    return j;
                }
            }

            int k = WorldRenderer.getPackedLightmapCoords(lightReaderIn, blockStateIn, blockPosIn);
            if (this.enabled) {
                if (this.packedLightCache.size() == 100) {
                    this.packedLightCache.removeFirstInt();
                }

                this.packedLightCache.put(i, k);
            }

            return k;
        }

        public float getBrightness(BlockState blockStateIn, IBlockDisplayReader lightReaderIn, BlockPos blockPosIn) {
            long i = blockPosIn.toLong();
            if (this.enabled) {
                float f = this.brightnessCache.get(i);
                if (!Float.isNaN(f)) {
                    return f;
                }
            }

            float f1 = blockStateIn.getAmbientOcclusionLightValue(lightReaderIn, blockPosIn);
            if (this.enabled) {
                if (this.brightnessCache.size() == 100) {
                    this.brightnessCache.removeFirstFloat();
                }

                this.brightnessCache.put(i, f1);
            }

            return f1;
        }
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
