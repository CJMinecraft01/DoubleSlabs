package cjminecraft.doubleslabs.test.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;

public class SlimeSlab extends SlabBlock {
    public SlimeSlab() {
        super(Properties.of(Material.CLAY, MaterialColor.GRASS).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion());
    }

    @Override
    public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSuppressingBounce()) {
            super.fallOn(worldIn, state, pos, entityIn, fallDistance);
        } else {
            entityIn.causeFallDamage(fallDistance, 0.0F, DamageSource.FALL);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter world, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(world, entity);
        } else {
            this.bounceEntity(entity);
        }
    }

    private void bounceEntity(Entity entity) {
        Vec3 motion = entity.getDeltaMovement();
        if (motion.y < 0.0D) {
            double ySpeed = entity instanceof LivingEntity ? 1.0D : 0.8D;
            entity.setDeltaMovement(motion.x, -motion.y * ySpeed, motion.z);
        }
    }

    @Override
    public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        double d0 = Math.abs(entityIn.getDeltaMovement().y);
        if (d0 < 0.1D && !entityIn.isSteppingCarefully()) {
            double d1 = 0.4D + d0 * 0.2D;
            entityIn.setDeltaMovement(entityIn.getDeltaMovement().multiply(d1, 1.0D, d1));
        }

        super.stepOn(worldIn, pos, state, entityIn);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (adjacentBlockState.getBlock() == this) {
            SlabType type = state.getValue(BlockStateProperties.SLAB_TYPE);
            SlabType otherType = adjacentBlockState.getValue(BlockStateProperties.SLAB_TYPE);
            if (side == Direction.UP)
                return (type == SlabType.DOUBLE || type == SlabType.TOP) &&  (otherType == SlabType.DOUBLE || otherType == SlabType.BOTTOM);
            else if (side == Direction.DOWN)
                return (type == SlabType.DOUBLE || type == SlabType.BOTTOM) &&  (otherType == SlabType.DOUBLE || otherType == SlabType.TOP);
            return adjacentBlockState.getValue(BlockStateProperties.SLAB_TYPE).equals(state.getValue(BlockStateProperties.SLAB_TYPE)) || adjacentBlockState.getValue(BlockStateProperties.SLAB_TYPE).equals(SlabType.DOUBLE);
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }
}
