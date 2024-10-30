package cjminecraft.doubleslabs.fabric.platform;

import cjminecraft.doubleslabs.common.block.DynamicDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.fabric.block.FabricDynamicDoubleSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class FabricBlockFactory implements IPlatformBlockFactory {
    @Override
    public DynamicDoubleSlabBlock createDynamicDoubleSlabBlock() {
        return new FabricDynamicDoubleSlabBlock(BlockBehaviour.Properties.of());
    }
}
