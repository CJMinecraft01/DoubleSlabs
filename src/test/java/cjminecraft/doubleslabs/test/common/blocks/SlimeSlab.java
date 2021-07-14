package cjminecraft.doubleslabs.test.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlimeSlab extends SlabBlock {
    public SlimeSlab() {
        super(Properties.create(Material.CLAY, MaterialColor.GRASS).slipperiness(0.8F).sound(SoundType.SLIME));
    }

    @Override
    public boolean isSolid(BlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSneaking()) {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        } else {
            entityIn.fall(fallDistance, 0.0F);
        }

    }

    @Override
    public void onLanded(IBlockReader worldIn, Entity entityIn) {
        if (entityIn.isSneaking()) {
            super.onLanded(worldIn, entityIn);
        } else {
            Vec3d vec3d = entityIn.getMotion();
            if (vec3d.y < 0.0D) {
                double d0 = entityIn instanceof LivingEntity ? 1.0D : 0.8D;
                entityIn.setMotion(vec3d.x, -vec3d.y * d0, vec3d.z);
            }
        }

    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        double d0 = Math.abs(entityIn.getMotion().y);
        if (d0 < 0.1D && !entityIn.isSneaking()) {
            double d1 = 0.4D + d0 * 0.2D;
            entityIn.setMotion(entityIn.getMotion().mul(d1, 1.0D, d1));
        }

        super.onEntityWalk(worldIn, pos, entityIn);
    }


    @OnlyIn(Dist.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (adjacentBlockState.getBlock() == this) {
            SlabType type = state.get(BlockStateProperties.SLAB_TYPE);
            SlabType otherType = adjacentBlockState.get(BlockStateProperties.SLAB_TYPE);
            if (side == Direction.UP)
                return (type == SlabType.DOUBLE || type == SlabType.TOP) &&  (otherType == SlabType.DOUBLE || otherType == SlabType.BOTTOM);
            else if (side == Direction.DOWN)
                return (type == SlabType.DOUBLE || type == SlabType.BOTTOM) &&  (otherType == SlabType.DOUBLE || otherType == SlabType.TOP);
            return adjacentBlockState.get(BlockStateProperties.SLAB_TYPE).equals(state.get(BlockStateProperties.SLAB_TYPE)) || adjacentBlockState.get(BlockStateProperties.SLAB_TYPE).equals(SlabType.DOUBLE);
        }
        return super.isSideInvisible(state, adjacentBlockState, side);
    }
}
