package cjminecraft.doubleslabs.neoforge.platform;

import cjminecraft.doubleslabs.common.block.DynamicDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.neoforge.block.NeoForgeDynamicDoubleSlabBlock;

public class NeoForgeBlockFactory implements IPlatformBlockFactory {
    @Override
    public DynamicDoubleSlabBlock createDynamicDoubleSlabBlock() {
        return new NeoForgeDynamicDoubleSlabBlock();
    }
}
