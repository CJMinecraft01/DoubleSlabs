package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.neoforge.common.block.NeoForgeMixedDoubleSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class NeoForgeBlockFactory implements IPlatformBlockFactory {
    @Override
    public MixedDoubleSlabBlock createMixedDoubleSlabBlock() {
        return new NeoForgeMixedDoubleSlabBlock(BlockBehaviour.Properties.of());
    }
}
