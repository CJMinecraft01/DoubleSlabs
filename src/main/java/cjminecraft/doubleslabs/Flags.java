package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.HashMap;

public class Flags {

    private static final HashMap<BlockPos, Boolean> POSITIVE = new HashMap<>();

    public static boolean isPositive(BlockPos pos) {
        return POSITIVE.getOrDefault(pos, false);
    }

    public static TileEntity getTileEntityAtPos(BlockPos pos, IBlockReader world) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityVerticalSlab)
            return isPositive(pos) ? ((TileEntityVerticalSlab) tile).getPositiveTile() : ((TileEntityVerticalSlab) tile).getNegativeTile();
        return tile;
    }

    public static BlockState getBlockStateAtPos(BlockPos pos, IBlockReader world) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityVerticalSlab)
            return isPositive(pos) ? ((TileEntityVerticalSlab) tile).getPositiveState() : ((TileEntityVerticalSlab) tile).getNegativeState();
        return world.getBlockState(pos);
    }

    public static void setPositive(BlockPos pos, boolean value) {
        POSITIVE.put(pos, value);
    }

}
