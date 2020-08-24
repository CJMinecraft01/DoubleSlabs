package cjminecraft.doubleslabs.api;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBlockInfo {

    @Nullable
    BlockState getBlockState();

    @Nullable
    TileEntity getTileEntity();

    void setBlockState(@Nullable BlockState state);

    void setTileEntity(@Nullable TileEntity tile);

    @Nonnull
    World getWorld();

    boolean isPositive();

    BlockPos getPos();

}
