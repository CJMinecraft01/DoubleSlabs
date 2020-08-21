package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.IBlockInfo;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.Optional;

public class DoubleSlabBlock extends DynamicSlabBlock {

    protected static Optional<IBlockInfo> getHalfState(IBlockReader world, BlockPos pos, double y) {
        return getTile(world, pos).flatMap(tile -> tile.getNegativeBlockInfo().getBlockState() == null && tile.getPositiveBlockInfo().getBlockState() == null ? Optional.empty() :
                (y > 0.5 || tile.getNegativeBlockInfo().getBlockState() == null) && tile.getPositiveBlockInfo().getBlockState() != null ?
                        Optional.of(tile.getPositiveBlockInfo()) : Optional.of(tile.getNegativeBlockInfo()));
    }

}
