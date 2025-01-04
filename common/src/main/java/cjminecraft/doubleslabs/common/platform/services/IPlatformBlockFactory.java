package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;

import java.util.function.Supplier;

public interface IPlatformBlockFactory {

    Supplier<MixedDoubleSlabBlock> getMixedSlabBlock();

}
