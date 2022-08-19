package cjminecraft.doubleslabs.fabric.api;

import cjminecraft.doubleslabs.api.BlockInfo;
import cjminecraft.doubleslabs.api.ILevelWrapper;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import net.minecraft.world.level.Level;

public class FabricBlockInfo extends BlockInfo {
    public FabricBlockInfo(SlabBlockEntity<?> slab, boolean positive) {
        super(slab, positive);
    }

    @Override
    public ILevelWrapper<?> createWrappedLevel(Level level) {
        // todo
        return null;
    }
}
