package cjminecraft.doubleslabs.fabric.common.block.entity;

import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.fabric.api.FabricBlockInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricSlabBlockEntity extends SlabBlockEntity<FabricBlockInfo> {
    public FabricSlabBlockEntity(BlockPos pos, BlockState state, FabricBlockInfo negativeBlockInfo, FabricBlockInfo positiveBlockInfo) {
        super(pos, state, negativeBlockInfo, positiveBlockInfo);
    }

    @Override
    public void markDirtyClient() {

    }
}
