package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;

import java.util.function.Supplier;

public class FabricBlockFactory implements IPlatformBlockFactory {
    @Override
    public Supplier<MixedDoubleSlabBlock> getMixedSlabBlock() {
        return () -> DSFabricBlocks.MIXED_SLAB;
    }
}
