package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DoubleSlabs.MODID);

    public static final RegistryObject<DynamicSlabBlock> DOUBLE_SLAB = BLOCKS.register("double_slab", DoubleSlabBlock::new);
    public static final RegistryObject<DynamicSlabBlock> VERTICAL_SLAB = BLOCKS.register("vertical_slab", VerticalSlabBlock::new);

    public static final RegistryObject<RaisedCampfireBlock> RAISED_CAMPFIRE = BLOCKS.register("raised_campfire", () -> new RaisedCampfireBlock(Blocks.CAMPFIRE, 1, Block.Properties.from(Blocks.CAMPFIRE)));

}
