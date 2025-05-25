package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlocks;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlocks;

public class ForgeBlocks implements IPlatformBlocks {
    @Override
    public MixedDoubleSlabBlock getMixedSlabBlock() {
        return DSForgeBlocks.MIXED_SLAB.get();
    }

    @Override
    public MixedDoubleSlabBlock getTransparentMixedSlabBlock() {
        return DSForgeBlocks.TRANSPARENT_MIXED_SLAB.get();
    }

    @Override
    public VerticalSlabBlock getVerticalSlabBlock() {
        return DSForgeBlocks.VERTICAL_SLAB.get();
    }
}
