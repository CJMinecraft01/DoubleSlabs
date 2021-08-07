package cjminecraft.doubleslabs.test.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GlassSlab extends SlabBlock {
    public GlassSlab() {
        super(Properties.of(Material.GLASS).strength(0.3f).noOcclusion().isSuffocating((state, world, pos) -> true).isValidSpawn((block, state, pos, entity) -> false));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
}
