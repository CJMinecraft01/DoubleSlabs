package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.block.DynamicDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.neoforge.common.block.NeoForgeDynamicDoubleSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class NeoForgeBlockFactory implements IPlatformBlockFactory {
    @Override
    public DynamicDoubleSlabBlock createDynamicDoubleSlabBlock() {
        return new NeoForgeDynamicDoubleSlabBlock(BlockBehaviour.Properties.of());
    }
}
