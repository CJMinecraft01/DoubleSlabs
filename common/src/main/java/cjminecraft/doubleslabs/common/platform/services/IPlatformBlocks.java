package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;

import java.util.function.Supplier;

public interface IPlatformBlocks {

    Supplier<MixedDoubleSlabBlock> getMixedSlabBlock();

}
