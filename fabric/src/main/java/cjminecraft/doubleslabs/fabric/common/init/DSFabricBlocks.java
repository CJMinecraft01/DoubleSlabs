package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.fabric.common.block.FabricMixedDoubleSlabBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import static cjminecraft.doubleslabs.common.init.DSBlocks.*;

public class DSFabricBlocks {

    public static final MixedDoubleSlabBlock MIXED_SLAB = new FabricMixedDoubleSlabBlock(MIXED_SLAB_PROPERTIES);

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, MIXED_SLAB_ID, MIXED_SLAB);
    }

}
