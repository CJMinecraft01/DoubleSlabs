package cjminecraft.doubleslabs.client.util;

import net.minecraft.Util;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.BitSet;

@OnlyIn(Dist.CLIENT)
public class AmbientOcclusionFace {

    private static final Direction[] DIRECTIONS = Direction.values();

    public final float[] brightness = new float[4];
    public final int[] lightmap = new int[4];

    public AmbientOcclusionFace() {
    }

    public void calculate(BlockAndTintGetter p_111168_, BlockState p_111169_, BlockPos p_111170_, Direction p_111171_, float[] p_111172_, BitSet p_111173_, boolean p_111174_) {
        BlockPos blockpos = p_111173_.get(0) ? p_111170_.relative(p_111171_) : p_111170_;
        AdjacencyInfo modelblockrenderer$adjacencyinfo = AdjacencyInfo.fromFacing(p_111171_);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        ModelBlockRenderer.Cache modelblockrenderer$cache = ModelBlockRenderer.CACHE.get();
        blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[0]);
        BlockState blockstate = p_111168_.getBlockState(blockpos$mutableblockpos);
        int i = modelblockrenderer$cache.getLightColor(blockstate, p_111168_, blockpos$mutableblockpos);
        float f = modelblockrenderer$cache.getShadeBrightness(blockstate, p_111168_, blockpos$mutableblockpos);
        blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[1]);
        BlockState blockstate1 = p_111168_.getBlockState(blockpos$mutableblockpos);
        int j = modelblockrenderer$cache.getLightColor(blockstate1, p_111168_, blockpos$mutableblockpos);
        float f1 = modelblockrenderer$cache.getShadeBrightness(blockstate1, p_111168_, blockpos$mutableblockpos);
        blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[2]);
        BlockState blockstate2 = p_111168_.getBlockState(blockpos$mutableblockpos);
        int k = modelblockrenderer$cache.getLightColor(blockstate2, p_111168_, blockpos$mutableblockpos);
        float f2 = modelblockrenderer$cache.getShadeBrightness(blockstate2, p_111168_, blockpos$mutableblockpos);
        blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[3]);
        BlockState blockstate3 = p_111168_.getBlockState(blockpos$mutableblockpos);
        int l = modelblockrenderer$cache.getLightColor(blockstate3, p_111168_, blockpos$mutableblockpos);
        float f3 = modelblockrenderer$cache.getShadeBrightness(blockstate3, p_111168_, blockpos$mutableblockpos);
        BlockState blockstate4 = p_111168_.getBlockState(blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[0]).move(p_111171_));
        boolean flag = !blockstate4.isViewBlocking(p_111168_, blockpos$mutableblockpos) || blockstate4.getLightBlock(p_111168_, blockpos$mutableblockpos) == 0;
        BlockState blockstate5 = p_111168_.getBlockState(blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[1]).move(p_111171_));
        boolean flag1 = !blockstate5.isViewBlocking(p_111168_, blockpos$mutableblockpos) || blockstate5.getLightBlock(p_111168_, blockpos$mutableblockpos) == 0;
        BlockState blockstate6 = p_111168_.getBlockState(blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[2]).move(p_111171_));
        boolean flag2 = !blockstate6.isViewBlocking(p_111168_, blockpos$mutableblockpos) || blockstate6.getLightBlock(p_111168_, blockpos$mutableblockpos) == 0;
        BlockState blockstate7 = p_111168_.getBlockState(blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[3]).move(p_111171_));
        boolean flag3 = !blockstate7.isViewBlocking(p_111168_, blockpos$mutableblockpos) || blockstate7.getLightBlock(p_111168_, blockpos$mutableblockpos) == 0;
        float f4;
        int i1;
        if (!flag2 && !flag) {
            f4 = f;
            i1 = i;
        } else {
            blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[0]).move(modelblockrenderer$adjacencyinfo.corners[2]);
            BlockState blockstate8 = p_111168_.getBlockState(blockpos$mutableblockpos);
            f4 = modelblockrenderer$cache.getShadeBrightness(blockstate8, p_111168_, blockpos$mutableblockpos);
            i1 = modelblockrenderer$cache.getLightColor(blockstate8, p_111168_, blockpos$mutableblockpos);
        }

        float f5;
        int j1;
        if (!flag3 && !flag) {
            f5 = f;
            j1 = i;
        } else {
            blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[0]).move(modelblockrenderer$adjacencyinfo.corners[3]);
            BlockState blockstate10 = p_111168_.getBlockState(blockpos$mutableblockpos);
            f5 = modelblockrenderer$cache.getShadeBrightness(blockstate10, p_111168_, blockpos$mutableblockpos);
            j1 = modelblockrenderer$cache.getLightColor(blockstate10, p_111168_, blockpos$mutableblockpos);
        }

        float f6;
        int k1;
        if (!flag2 && !flag1) {
            f6 = f;
            k1 = i;
        } else {
            blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[1]).move(modelblockrenderer$adjacencyinfo.corners[2]);
            BlockState blockstate11 = p_111168_.getBlockState(blockpos$mutableblockpos);
            f6 = modelblockrenderer$cache.getShadeBrightness(blockstate11, p_111168_, blockpos$mutableblockpos);
            k1 = modelblockrenderer$cache.getLightColor(blockstate11, p_111168_, blockpos$mutableblockpos);
        }

        float f7;
        int l1;
        if (!flag3 && !flag1) {
            f7 = f;
            l1 = i;
        } else {
            blockpos$mutableblockpos.setWithOffset(blockpos, modelblockrenderer$adjacencyinfo.corners[1]).move(modelblockrenderer$adjacencyinfo.corners[3]);
            BlockState blockstate12 = p_111168_.getBlockState(blockpos$mutableblockpos);
            f7 = modelblockrenderer$cache.getShadeBrightness(blockstate12, p_111168_, blockpos$mutableblockpos);
            l1 = modelblockrenderer$cache.getLightColor(blockstate12, p_111168_, blockpos$mutableblockpos);
        }

        int i3 = modelblockrenderer$cache.getLightColor(p_111169_, p_111168_, p_111170_);
        blockpos$mutableblockpos.setWithOffset(p_111170_, p_111171_);
        BlockState blockstate9 = p_111168_.getBlockState(blockpos$mutableblockpos);
        if (p_111173_.get(0) || !blockstate9.isSolidRender(p_111168_, blockpos$mutableblockpos)) {
            i3 = modelblockrenderer$cache.getLightColor(blockstate9, p_111168_, blockpos$mutableblockpos);
        }

        float f8 = p_111173_.get(0) ? modelblockrenderer$cache.getShadeBrightness(p_111168_.getBlockState(blockpos), p_111168_, blockpos) : modelblockrenderer$cache.getShadeBrightness(p_111168_.getBlockState(p_111170_), p_111168_, p_111170_);
        AmbientVertexRemap modelblockrenderer$ambientvertexremap = AmbientVertexRemap.fromFacing(p_111171_);
        if (p_111173_.get(1) && modelblockrenderer$adjacencyinfo.doNonCubicWeight) {
            float f29 = (f3 + f + f5 + f8) * 0.25F;
            float f31 = (f2 + f + f4 + f8) * 0.25F;
            float f32 = (f2 + f1 + f6 + f8) * 0.25F;
            float f33 = (f3 + f1 + f7 + f8) * 0.25F;
            float f13 = p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[0].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[1].shape];
            float f14 = p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[2].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[3].shape];
            float f15 = p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[4].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[5].shape];
            float f16 = p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[6].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[7].shape];
            float f17 = p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[0].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[1].shape];
            float f18 = p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[2].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[3].shape];
            float f19 = p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[4].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[5].shape];
            float f20 = p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[6].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[7].shape];
            float f21 = p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[0].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[1].shape];
            float f22 = p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[2].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[3].shape];
            float f23 = p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[4].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[5].shape];
            float f24 = p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[6].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[7].shape];
            float f25 = p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[0].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[1].shape];
            float f26 = p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[2].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[3].shape];
            float f27 = p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[4].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[5].shape];
            float f28 = p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[6].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[7].shape];
            this.brightness[modelblockrenderer$ambientvertexremap.vert0] = f29 * f13 + f31 * f14 + f32 * f15 + f33 * f16;
            this.brightness[modelblockrenderer$ambientvertexremap.vert1] = f29 * f17 + f31 * f18 + f32 * f19 + f33 * f20;
            this.brightness[modelblockrenderer$ambientvertexremap.vert2] = f29 * f21 + f31 * f22 + f32 * f23 + f33 * f24;
            this.brightness[modelblockrenderer$ambientvertexremap.vert3] = f29 * f25 + f31 * f26 + f32 * f27 + f33 * f28;
            int i2 = this.blend(l, i, j1, i3);
            int j2 = this.blend(k, i, i1, i3);
            int k2 = this.blend(k, j, k1, i3);
            int l2 = this.blend(l, j, l1, i3);
            this.lightmap[modelblockrenderer$ambientvertexremap.vert0] = this.blend(i2, j2, k2, l2, f13, f14, f15, f16);
            this.lightmap[modelblockrenderer$ambientvertexremap.vert1] = this.blend(i2, j2, k2, l2, f17, f18, f19, f20);
            this.lightmap[modelblockrenderer$ambientvertexremap.vert2] = this.blend(i2, j2, k2, l2, f21, f22, f23, f24);
            this.lightmap[modelblockrenderer$ambientvertexremap.vert3] = this.blend(i2, j2, k2, l2, f25, f26, f27, f28);
        } else {
            float f9 = (f3 + f + f5 + f8) * 0.25F;
            float f10 = (f2 + f + f4 + f8) * 0.25F;
            float f11 = (f2 + f1 + f6 + f8) * 0.25F;
            float f12 = (f3 + f1 + f7 + f8) * 0.25F;
            this.lightmap[modelblockrenderer$ambientvertexremap.vert0] = this.blend(l, i, j1, i3);
            this.lightmap[modelblockrenderer$ambientvertexremap.vert1] = this.blend(k, i, i1, i3);
            this.lightmap[modelblockrenderer$ambientvertexremap.vert2] = this.blend(k, j, k1, i3);
            this.lightmap[modelblockrenderer$ambientvertexremap.vert3] = this.blend(l, j, l1, i3);
            this.brightness[modelblockrenderer$ambientvertexremap.vert0] = f9;
            this.brightness[modelblockrenderer$ambientvertexremap.vert1] = f10;
            this.brightness[modelblockrenderer$ambientvertexremap.vert2] = f11;
            this.brightness[modelblockrenderer$ambientvertexremap.vert3] = f12;
        }

        float f30 = p_111168_.getShade(p_111171_, p_111174_);

        for(int j3 = 0; j3 < this.brightness.length; ++j3) {
            this.brightness[j3] *= f30;
        }

    }

    private int blend(int p_111154_, int p_111155_, int p_111156_, int p_111157_) {
        if (p_111154_ == 0) {
            p_111154_ = p_111157_;
        }

        if (p_111155_ == 0) {
            p_111155_ = p_111157_;
        }

        if (p_111156_ == 0) {
            p_111156_ = p_111157_;
        }

        return p_111154_ + p_111155_ + p_111156_ + p_111157_ >> 2 & 16711935;
    }

    private int blend(int p_111159_, int p_111160_, int p_111161_, int p_111162_, float p_111163_, float p_111164_, float p_111165_, float p_111166_) {
        int i = (int)((float)(p_111159_ >> 16 & 255) * p_111163_ + (float)(p_111160_ >> 16 & 255) * p_111164_ + (float)(p_111161_ >> 16 & 255) * p_111165_ + (float)(p_111162_ >> 16 & 255) * p_111166_) & 255;
        int j = (int)((float)(p_111159_ & 255) * p_111163_ + (float)(p_111160_ & 255) * p_111164_ + (float)(p_111161_ & 255) * p_111165_ + (float)(p_111162_ & 255) * p_111166_) & 255;
        return i << 16 | j;
    }

    @OnlyIn(Dist.CLIENT)
    protected static enum AdjacencyInfo {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new SizeInfo[]{SizeInfo.FLIP_WEST, SizeInfo.SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.FLIP_WEST, SizeInfo.NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_EAST, SizeInfo.NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_EAST, SizeInfo.SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new SizeInfo[]{SizeInfo.EAST, SizeInfo.SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.EAST, SizeInfo.NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.WEST, SizeInfo.NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.WEST, SizeInfo.SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST}, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.UP, SizeInfo.WEST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.DOWN, SizeInfo.WEST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.DOWN, SizeInfo.EAST}, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.UP, SizeInfo.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.UP, SizeInfo.NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new SizeInfo[]{SizeInfo.FLIP_DOWN, SizeInfo.SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.FLIP_DOWN, SizeInfo.NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_UP, SizeInfo.NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_UP, SizeInfo.SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.SOUTH});

        final Direction[] corners;
        final boolean doNonCubicWeight;
        final SizeInfo[] vert0Weights;
        final SizeInfo[] vert1Weights;
        final SizeInfo[] vert2Weights;
        final SizeInfo[] vert3Weights;
        private static final AdjacencyInfo[] BY_FACING = Util.make(new AdjacencyInfo[6], (p_111134_) -> {
            p_111134_[Direction.DOWN.get3DDataValue()] = DOWN;
            p_111134_[Direction.UP.get3DDataValue()] = UP;
            p_111134_[Direction.NORTH.get3DDataValue()] = NORTH;
            p_111134_[Direction.SOUTH.get3DDataValue()] = SOUTH;
            p_111134_[Direction.WEST.get3DDataValue()] = WEST;
            p_111134_[Direction.EAST.get3DDataValue()] = EAST;
        });

        private AdjacencyInfo(Direction[] p_111122_, float p_111123_, boolean p_111124_, SizeInfo[] p_111125_, SizeInfo[] p_111126_, SizeInfo[] p_111127_, SizeInfo[] p_111128_) {
            this.corners = p_111122_;
            this.doNonCubicWeight = p_111124_;
            this.vert0Weights = p_111125_;
            this.vert1Weights = p_111126_;
            this.vert2Weights = p_111127_;
            this.vert3Weights = p_111128_;
        }

        public static AdjacencyInfo fromFacing(Direction p_111132_) {
            return BY_FACING[p_111132_.get3DDataValue()];
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static enum SizeInfo {
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

        final int shape;

        private SizeInfo(Direction p_111264_, boolean p_111265_) {
            this.shape = p_111264_.get3DDataValue() + (p_111265_ ? DIRECTIONS.length : 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum AmbientVertexRemap {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        final int vert0;
        final int vert1;
        final int vert2;
        final int vert3;
        private static final AmbientVertexRemap[] BY_FACING = Util.make(new AmbientVertexRemap[6], (p_111204_) -> {
            p_111204_[Direction.DOWN.get3DDataValue()] = DOWN;
            p_111204_[Direction.UP.get3DDataValue()] = UP;
            p_111204_[Direction.NORTH.get3DDataValue()] = NORTH;
            p_111204_[Direction.SOUTH.get3DDataValue()] = SOUTH;
            p_111204_[Direction.WEST.get3DDataValue()] = WEST;
            p_111204_[Direction.EAST.get3DDataValue()] = EAST;
        });

        private AmbientVertexRemap(int p_111195_, int p_111196_, int p_111197_, int p_111198_) {
            this.vert0 = p_111195_;
            this.vert1 = p_111196_;
            this.vert2 = p_111197_;
            this.vert3 = p_111198_;
        }

        public static AmbientVertexRemap fromFacing(Direction p_111202_) {
            return BY_FACING[p_111202_.get3DDataValue()];
        }
    }
    
}
