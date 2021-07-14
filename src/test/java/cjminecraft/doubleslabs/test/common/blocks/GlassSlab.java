package cjminecraft.doubleslabs.test.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GlassSlab extends SlabBlock {
    public GlassSlab() {
        super(Properties.create(Material.GLASS).hardnessAndResistance(0.3f));
    }

    @Override
    public boolean isSolid(BlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
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

    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
        return false;
    }
}
