package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.HashMap;

@Deprecated
public class Flags {

    private static final HashMap<BlockPos, Boolean> POSITIVE = new HashMap<>();

    public static boolean isPositive(BlockPos pos) {
        return POSITIVE.getOrDefault(pos, false);
    }

    public static TileEntity getTileEntityAtPos(BlockPos pos, IBlockReader world) {
        TileEntity tile = world.getTileEntity(pos);
//        if (tile instanceof SlabTileEntity)
//            return isPositive(pos) ? ((SlabTileEntity) tile).getPositiveBlockInfo().getTileEntity() : ((SlabTileEntity) tile).getNegativeBlockInfo().getTileEntity();
        return tile;
    }

    public static BlockState getBlockStateAtPos(BlockPos pos, IBlockReader world) {
        TileEntity tile = world.getTileEntity(pos);
//        if (tile instanceof SlabTileEntity)
//            return isPositive(pos) ? ((SlabTileEntity) tile).getPositiveBlockInfo().getBlockState() : ((SlabTileEntity) tile).getNegativeBlockInfo().getBlockState();
        return world.getBlockState(pos);
    }

    public static void setPositive(BlockPos pos, boolean value) {
        POSITIVE.put(pos, value);
    }

}
