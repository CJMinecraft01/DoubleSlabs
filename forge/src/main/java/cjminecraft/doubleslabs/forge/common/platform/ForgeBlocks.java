package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlocks;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlocks;

public class ForgeBlocks implements IPlatformBlocks {
    @Override
    public MixedDoubleSlabBlock getMixedSlabBlock() {
        return DSForgeBlocks.MIXED_SLAB.get();
    }
}
