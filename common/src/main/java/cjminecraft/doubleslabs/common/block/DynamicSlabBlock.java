package cjminecraft.doubleslabs.common.block;

import net.minecraft.world.level.block.Block;

public abstract class DynamicSlabBlock extends Block {
    // Anything common to horizontal and vertical dynamic slabs should go here

    public DynamicSlabBlock() {
        super(Properties.of());
    }
}
