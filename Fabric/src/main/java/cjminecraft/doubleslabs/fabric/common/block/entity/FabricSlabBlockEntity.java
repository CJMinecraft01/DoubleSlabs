package cjminecraft.doubleslabs.fabric.common.block.entity;

import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.fabric.api.FabricBlockInfo;
import cjminecraft.doubleslabs.fabric.common.init.DSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricSlabBlockEntity extends SlabBlockEntity<FabricBlockInfo> {

    public FabricSlabBlockEntity(BlockPos pos, BlockState state) {
        super(DSBlockEntities.DYNAMIC_SLAB, pos, state);
        this.negativeBlockInfo = new FabricBlockInfo(this, false);
        this.positiveBlockInfo = new FabricBlockInfo(this, true);
    }

    @Override
    public void markDirtyClient() {

    }
}
