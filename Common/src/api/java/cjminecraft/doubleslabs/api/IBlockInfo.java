package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.ISlabSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IBlockInfo {

    @Nullable
    BlockState getBlockState();

    default Optional<BlockState> blockState() {
        BlockState state = getBlockState();
        return state == null ? Optional.empty() : Optional.of(state);
    }

    @Nullable
    BlockEntity getBlockEntity();

    default Optional<BlockEntity> blockEntity() {
        BlockEntity blockEntity = getBlockEntity();
        return blockEntity == null ? Optional.empty() : Optional.of(blockEntity);
    }

    void setBlockState(@Nullable BlockState state);

    void setBlockEntity(@Nullable BlockEntity tile);

    @NotNull
    Level getLevel();

    boolean isPositive();

    BlockPos getPos();

    @Nullable
    ISlabSupport getSupport();

}
