package cjminecraft.doubleslabs.library.state;

import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SlabStateContainer implements ISlabStateContainer {

    protected BlockState blockState;
    protected @Nullable BlockEntity blockEntity;

    public SlabStateContainer() {
        this.blockState = Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    @Override
    public void setBlockState(BlockState state) {
        this.blockState = state;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public void setBlockEntity(@Nullable BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }
}
