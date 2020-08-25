package cjminecraft.doubleslabs.test.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlimeSlab extends SlabBlock {
    public SlimeSlab() {
        super(Properties.create(Material.CLAY, MaterialColor.GRASS).slipperiness(0.8F).sound(SoundType.SLIME).notSolid());
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSuppressingBounce()) {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        } else {
            entityIn.onLivingFall(fallDistance, 0.0F);
        }
    }

    @Override
    public void onLanded(IBlockReader worldIn, Entity entityIn) {
        if (entityIn.isSuppressingBounce()) {
            super.onLanded(worldIn, entityIn);
        } else {
            this.bounceEntity(entityIn);
        }
    }

    private void bounceEntity(Entity entity) {
        Vector3d vector3d = entity.getMotion();
        if (vector3d.y < 0.0D) {
            double d0 = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setMotion(vector3d.x, -vector3d.y * d0, vector3d.z);
        }

    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        double d0 = Math.abs(entityIn.getMotion().y);
        if (d0 < 0.1D && !entityIn.isSteppingCarefully()) {
            double d1 = 0.4D + d0 * 0.2D;
            entityIn.setMotion(entityIn.getMotion().mul(d1, 1.0D, d1));
        }

        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.isIn(this) ? true : super.isSideInvisible(state, adjacentBlockState, side);
    }
}
