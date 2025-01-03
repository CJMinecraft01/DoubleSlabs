package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface IPlatformBlockFactory {

    MixedDoubleSlabBlock createMixedDoubleSlabBlock(BlockBehaviour.Properties properties);

}
