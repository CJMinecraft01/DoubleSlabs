package cjminecraft.doubleslabs.fabric.api;

import cjminecraft.doubleslabs.api.BlockInfo;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;

public class FabricBlockInfo extends BlockInfo {
    public FabricBlockInfo(SlabBlockEntity<?> slab, boolean positive) {
        super(slab, positive);
    }
}
