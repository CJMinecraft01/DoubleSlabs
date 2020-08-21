package cjminecraft.doubleslabs.test.common.init;

import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.blocks.GlassSlab;
import cjminecraft.doubleslabs.test.common.blocks.SlimeSlab;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class DSTBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DoubleSlabsTest.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DoubleSlabsTest.MODID);

    public static final RegistryObject<Block> GLASS_SLAB = register(BLOCKS.register("glass_slab", GlassSlab::new));
    public static final RegistryObject<Block> SLIME_SLAB = register(BLOCKS.register("slime_slab", SlimeSlab::new));

    public static RegistryObject<Block> register(RegistryObject<Block> block) {
        ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
