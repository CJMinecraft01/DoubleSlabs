package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.fabric.common.block.FabricMixedDoubleSlabBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import static cjminecraft.doubleslabs.common.init.DSBlocks.*;

public class DSFabricBlocks {

    public static final MixedDoubleSlabBlock MIXED_SLAB = new FabricMixedDoubleSlabBlock(MIXED_SLAB_PROPERTIES);
    public static final MixedDoubleSlabBlock TRANSPARENT_MIXED_SLAB = new FabricMixedDoubleSlabBlock(TRANSPARENT_MIXED_SLAB_PROPERTIES);
    public static final VerticalSlabBlock VERTICAL_SLAB = new VerticalSlabBlock(VERTICAL_SLAB_PROPERTIES);

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, MIXED_SLAB_ID, MIXED_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, TRANSPARENT_MIXED_SLAB_ID, TRANSPARENT_MIXED_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, VERTICAL_SLAB_ID, VERTICAL_SLAB);
    }

}
