package cjminecraft.doubleslabs.neoforge.common.block.entity;

import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.library.state.SlabStateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class NeoForgeDynamicSlabBlockEntity extends DynamicSlabBlockEntity<SlabStateContainer> {
    public NeoForgeDynamicSlabBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    protected SlabStateContainer createBlockStateContainer() {
        return new SlabStateContainer(worldPosition);
    }

}
