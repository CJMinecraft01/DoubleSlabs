package cjminecraft.doubleslabs.fabric.common.block.entity;

import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.library.state.SlabStateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricDynamicSlabBlockEntity extends DynamicSlabBlockEntity<SlabStateContainer> {
    public FabricDynamicSlabBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    protected SlabStateContainer createBlockStateContainer() {
        return new SlabStateContainer();
    }
}
