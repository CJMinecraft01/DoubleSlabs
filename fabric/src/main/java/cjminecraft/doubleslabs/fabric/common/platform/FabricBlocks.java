package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlocks;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;

import java.util.function.Supplier;

public class FabricBlocks implements IPlatformBlocks {
    @Override
    public Supplier<MixedDoubleSlabBlock> getMixedSlabBlock() {
        return () -> DSFabricBlocks.MIXED_SLAB;
    }
}
