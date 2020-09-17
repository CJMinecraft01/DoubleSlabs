package cjminecraft.doubleslabs.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.HashMap;

@Deprecated
public class Flags {

    private static final HashMap<BlockPos, Boolean> POSITIVE = new HashMap<>();

    public static boolean isPositive(BlockPos pos) {
        return POSITIVE.getOrDefault(pos, false);
    }

    public static TileEntity getTileEntityAtPos(BlockPos pos, IBlockAccess world) {
        //        if (tile instanceof SlabTileEntity)
//            return isPositive(pos) ? ((SlabTileEntity) tile).getPositiveBlockInfo().getTileEntity() : ((SlabTileEntity) tile).getNegativeBlockInfo().getTileEntity();
        return world.getTileEntity(pos);
    }

    public static IBlockState getBlockStateAtPos(BlockPos pos, IBlockAccess world) {
//        if (tile instanceof SlabTileEntity)
//            return isPositive(pos) ? ((SlabTileEntity) tile).getPositiveBlockInfo().getBlockState() : ((SlabTileEntity) tile).getNegativeBlockInfo().getBlockState();
        return world.getBlockState(pos);
    }

    public static void setPositive(BlockPos pos, boolean value) {
        POSITIVE.put(pos, value);
    }

}
