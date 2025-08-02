package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;

public interface IPlatformBlocks {

    MixedDoubleSlabBlock getMixedSlabBlock();

    MixedDoubleSlabBlock getTransparentMixedSlabBlock();

    VerticalSlabBlock getVerticalSlabBlock();

}
