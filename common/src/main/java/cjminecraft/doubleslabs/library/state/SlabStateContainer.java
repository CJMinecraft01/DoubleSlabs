package cjminecraft.doubleslabs.library.state;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SlabStateContainer implements ISlabStateContainer {

    private final IDynamicSlabStateContainer parentContainer;

    protected final BlockPos pos;
    protected @Nullable Level level;
    protected BlockState blockState;
    protected @Nullable BlockEntity blockEntity;

    public SlabStateContainer(IDynamicSlabStateContainer parentContainer, BlockPos pos) {
        this.parentContainer = parentContainer;
        this.pos = pos;
        this.blockState = Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    @Override
    public void setBlockState(BlockState state) {
        this.blockState = state;
        this.parentContainer.markDirty();
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public void setBlockEntity(@Nullable BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public BlockPos getBlockPos() {
        return pos;
    }

    @Nullable
    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(@Nullable Level level) {
        this.level = level;
    }

}
