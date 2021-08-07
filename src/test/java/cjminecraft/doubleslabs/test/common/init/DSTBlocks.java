package cjminecraft.doubleslabs.test.common.init;

import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.blocks.ChestSlab;
import cjminecraft.doubleslabs.test.common.blocks.GlassSlab;
import cjminecraft.doubleslabs.test.common.blocks.SlimeSlab;
import cjminecraft.doubleslabs.test.common.blocks.VerticalSlab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSTBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DoubleSlabsTest.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DoubleSlabsTest.MODID);

    public static final RegistryObject<Block> GLASS_SLAB = register(BLOCKS.register("glass_slab", GlassSlab::new));
    public static final RegistryObject<Block> SLIME_SLAB = register(BLOCKS.register("slime_slab", SlimeSlab::new));
    public static final RegistryObject<Block> VERTICAL_STONE_SLAB = register(BLOCKS.register("vertical_stone_slab", () -> new VerticalSlab(BlockBehaviour.Properties.of(Material.STONE))));
    public static final RegistryObject<Block> CHEST_SLAB = register(BLOCKS.register("chest_slab", ChestSlab::new));

    public static RegistryObject<Block> register(RegistryObject<Block> block) {
        ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
        return block;
    }
}
