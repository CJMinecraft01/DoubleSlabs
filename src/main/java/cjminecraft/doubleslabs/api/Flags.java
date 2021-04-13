package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.capability.blockhalf.BlockHalfCapability;
import cjminecraft.doubleslabs.api.capability.blockhalf.IBlockHalf;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.HashMap;

public class Flags {

    public static TileEntity getTileEntityAtPos(BlockPos pos, IBlockReader world) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof SlabTileEntity)
            return tile.getCapability(BlockHalfCapability.BLOCK_HALF).map(IBlockHalf::isPositiveHalf).orElseThrow(RuntimeException::new) ? ((SlabTileEntity) tile).getPositiveBlockInfo().getTileEntity() : ((SlabTileEntity) tile).getNegativeBlockInfo().getTileEntity();
        return tile;
    }

    public static BlockState getBlockStateAtPos(BlockPos pos, IBlockReader world) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof SlabTileEntity)
            return tile.getCapability(BlockHalfCapability.BLOCK_HALF).map(IBlockHalf::isPositiveHalf).orElseThrow(RuntimeException::new) ? ((SlabTileEntity) tile).getPositiveBlockInfo().getBlockState() : ((SlabTileEntity) tile).getNegativeBlockInfo().getBlockState();
        return world.getBlockState(pos);
    }

}
