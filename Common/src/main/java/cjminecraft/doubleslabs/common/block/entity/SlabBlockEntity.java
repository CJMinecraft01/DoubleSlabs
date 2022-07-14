package cjminecraft.doubleslabs.common.block.entity;

import cjminecraft.doubleslabs.api.IStateContainer;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SlabBlockEntity extends BlockEntity implements IStateContainer {
    public SlabBlockEntity(BlockPos pos, BlockState state) {
        super(DSBlockEntities.DYNAMIC_SLAB.get(), pos, state);
    }

    public abstract void markDirtyClient();

}
