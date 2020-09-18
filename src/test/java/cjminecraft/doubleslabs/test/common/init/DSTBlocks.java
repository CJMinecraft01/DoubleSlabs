package cjminecraft.doubleslabs.test.common.init;

import cjminecraft.doubleslabs.common.util.registry.SimpleRegistrar;
import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.blocks.ChestSlab;
import cjminecraft.doubleslabs.test.common.blocks.GlassSlab;
import cjminecraft.doubleslabs.test.common.blocks.SlimeSlab;
import cjminecraft.doubleslabs.test.common.blocks.VerticalSlab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DSTBlocks {

    public static final SimpleRegistrar<Block> BLOCKS = new SimpleRegistrar<>(ForgeRegistries.BLOCKS, DoubleSlabsTest.MODID);
    public static final SimpleRegistrar<Item> ITEMS = new SimpleRegistrar<>(ForgeRegistries.ITEMS, DoubleSlabsTest.MODID);

    public static final GlassSlab GLASS_SLAB = register(BLOCKS.register("glass_slab", new GlassSlab.Half()));
    public static final GlassSlab GLASS_DOUBLE_SLAB = BLOCKS.register("glass_double_slab", new GlassSlab.Double());
    public static final SlimeSlab SLIME_SLAB = register(BLOCKS.register("slime_slab", new SlimeSlab.Half()));
    public static final SlimeSlab SLIME_DOUBLE_SLAB = BLOCKS.register("slime_double_slab", new SlimeSlab.Double());
    public static final VerticalSlab VERTICAL_STONE_SLAB = register(BLOCKS.register("vertical_stone_slab", new VerticalSlab(Material.ROCK)));
    public static final ChestSlab CHEST_SLAB = register(BLOCKS.register("chest_slab", new ChestSlab.Half()));
    public static final ChestSlab CHEST_DOUBLE_SLAB = BLOCKS.register("chest_double_slab", new ChestSlab.Double());

    public static <T extends Block> T register(T block) {
        ITEMS.register(block.getRegistryName().getPath(), new ItemBlock(block));
        return block;
    }

}
