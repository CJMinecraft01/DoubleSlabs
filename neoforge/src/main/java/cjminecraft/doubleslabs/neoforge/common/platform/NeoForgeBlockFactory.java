package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeBlocks;

import java.util.function.Supplier;

public class NeoForgeBlockFactory implements IPlatformBlockFactory {
    @Override
    public Supplier<MixedDoubleSlabBlock> getMixedSlabBlock() {
        return DSNeoForgeBlocks.MIXED_SLAB;
    }
}
