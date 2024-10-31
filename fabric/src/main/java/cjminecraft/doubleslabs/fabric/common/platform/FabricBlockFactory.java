package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.fabric.common.block.FabricMixedDoubleSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class FabricBlockFactory implements IPlatformBlockFactory {
    @Override
    public MixedDoubleSlabBlock createMixedDoubleSlabBlock() {
        return new FabricMixedDoubleSlabBlock(BlockBehaviour.Properties.of());
    }
}
