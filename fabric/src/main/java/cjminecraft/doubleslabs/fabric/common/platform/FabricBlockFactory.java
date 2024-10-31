package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.block.DynamicDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.fabric.common.block.FabricDynamicDoubleSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class FabricBlockFactory implements IPlatformBlockFactory {
    @Override
    public DynamicDoubleSlabBlock createDynamicDoubleSlabBlock() {
        return new FabricDynamicDoubleSlabBlock(BlockBehaviour.Properties.of());
    }
}
