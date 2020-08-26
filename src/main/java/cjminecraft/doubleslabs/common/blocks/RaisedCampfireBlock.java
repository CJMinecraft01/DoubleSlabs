package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class RaisedCampfireBlock extends CampfireBlock {

    private static final VoxelShape NEW_SHAPE = SHAPE.withOffset(0, 0.5d, 0);

    public RaisedCampfireBlock(boolean smokey, int fireDamage, Properties properties) {
        super(smokey, fireDamage, properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return NEW_SHAPE;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new RaisedCampfireTileEntity();
    }
}
