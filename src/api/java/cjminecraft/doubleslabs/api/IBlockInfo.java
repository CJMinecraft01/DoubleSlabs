package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.ISlabSupport;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBlockInfo {

    @Nullable
    IBlockState getBlockState();

    @Nullable
    TileEntity getTileEntity();

    void setBlockState(@Nullable IBlockState state);

    void setTileEntity(@Nullable TileEntity tile);

    @Nonnull
    World getWorld();

    boolean isPositive();

    BlockPos getPos();

    @Nullable
    ISlabSupport getSupport();

}
