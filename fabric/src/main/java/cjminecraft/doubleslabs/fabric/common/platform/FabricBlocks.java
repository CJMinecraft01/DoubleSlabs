package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlocks;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;

public class FabricBlocks implements IPlatformBlocks {
    @Override
    public MixedDoubleSlabBlock getMixedSlabBlock() {
        return DSFabricBlocks.MIXED_SLAB;
    }

    @Override
    public MixedDoubleSlabBlock getTransparentMixedSlabBlock() {
        return DSFabricBlocks.TRANSPARENT_MIXED_SLAB;
    }

    @Override
    public VerticalSlabBlock getVerticalSlabBlock() {
        return DSFabricBlocks.VERTICAL_SLAB;
    }
}
