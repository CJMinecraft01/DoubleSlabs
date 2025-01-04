package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlocks;

import java.util.function.Supplier;

public class ForgeBlockFactory implements IPlatformBlockFactory {
    @Override
    public Supplier<MixedDoubleSlabBlock> getMixedSlabBlock() {
        return DSForgeBlocks.MIXED_SLAB;
    }
}
