package cjminecraft.doubleslabs.forge.platform;

import cjminecraft.doubleslabs.common.block.DynamicDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.forge.block.ForgeDynamicDoubleSlabBlock;

public class ForgeBlockFactory implements IPlatformBlockFactory {
    @Override
    public DynamicDoubleSlabBlock createDynamicDoubleSlabBlock() {
        return new ForgeDynamicDoubleSlabBlock();
    }
}
