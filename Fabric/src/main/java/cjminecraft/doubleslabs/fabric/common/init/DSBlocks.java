package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.IBlocks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class DSBlocks implements IBlocks {

    public static final DSBlocks INSTANCE = new DSBlocks();

    public static DynamicSlabBlock DOUBLE_SLAB;

    public static DynamicSlabBlock VERTICAL_SLAB;

    public static void register() {
        DOUBLE_SLAB = Registry.register(
                Registry.BLOCK,
                new ResourceLocation(Constants.MODID, "double_slab"),
                new DoubleSlabBlock()
        );
        VERTICAL_SLAB = Registry.register(
                Registry.BLOCK,
                new ResourceLocation(Constants.MODID, "vertical_slab"),
                new VerticalSlabBlock()
        );
    }

    @Override
    public DynamicSlabBlock getDoubleSlabBlock() {
        return DOUBLE_SLAB;
    }

    @Override
    public DynamicSlabBlock getVerticalSlabBlock() {
        return VERTICAL_SLAB;
    }
}
