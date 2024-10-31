package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockFactory;
import cjminecraft.doubleslabs.forge.common.block.ForgeMixedDoubleSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ForgeBlockFactory implements IPlatformBlockFactory {
    @Override
    public MixedDoubleSlabBlock createMixedDoubleSlabBlock() {
        return new ForgeMixedDoubleSlabBlock(BlockBehaviour.Properties.of());
    }
}
