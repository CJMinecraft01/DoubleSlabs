package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.util.registry.SimpleRegistrar;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DSBlocks {

    public static final SimpleRegistrar<Block> BLOCKS = new SimpleRegistrar<>(ForgeRegistries.BLOCKS, DoubleSlabs.MODID);

    public static final DynamicSlabBlock DOUBLE_SLAB = BLOCKS.register("double_slab", new DoubleSlabBlock());
    public static final DynamicSlabBlock VERTICAL_SLAB = BLOCKS.register("vertical_slab", new VerticalSlabBlock());

}
