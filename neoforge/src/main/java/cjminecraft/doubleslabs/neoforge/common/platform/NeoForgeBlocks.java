package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlocks;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeBlocks;

public class NeoForgeBlocks implements IPlatformBlocks {
    @Override
    public MixedDoubleSlabBlock getMixedSlabBlock() {
        return DSNeoForgeBlocks.MIXED_SLAB.get();
    }

    @Override
    public MixedDoubleSlabBlock getTransparentMixedSlabBlock() {
        return DSNeoForgeBlocks.TRANSPARENT_MIXED_SLAB.get();
    }

    @Override
    public VerticalSlabBlock getVerticalSlabBlock() {
        return DSNeoForgeBlocks.VERTICAL_SLAB.get();
    }
}
