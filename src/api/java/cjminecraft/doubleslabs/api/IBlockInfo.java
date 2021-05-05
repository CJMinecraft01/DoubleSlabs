package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.ISlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface IBlockInfo {

    @Nullable
    BlockState getBlockState();

    default Optional<BlockState> getState() {
        BlockState state = getBlockState();
        return state == null ? Optional.empty() : Optional.of(state);
    }

    @Nullable
    TileEntity getTileEntity();

    default Optional<TileEntity> getTile() {
        TileEntity tile = getTileEntity();
        return tile == null ? Optional.empty() : Optional.of(tile);
    }

    void setBlockState(@Nullable BlockState state);

    void setTileEntity(@Nullable TileEntity tile);

    @Nonnull
    World getWorld();

    boolean isPositive();

    BlockPos getPos();

    @Nullable
    ISlabSupport getSupport();

}
