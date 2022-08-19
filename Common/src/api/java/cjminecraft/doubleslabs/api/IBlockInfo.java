package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.ISlabSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBlockInfo {

    @Nullable
    BlockState getBlockState();

    @Nullable
    BlockEntity getBlockEntity();

    void setBlockState(@Nullable BlockState state);

    void setBlockEntity(@Nullable BlockEntity tile);

    @Nonnull
    Level getLevel();

    boolean isPositive();

    BlockPos getPos();

    @Nullable
    ISlabSupport getSupport();

}
