package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.util.ModSpecificDeferredRegister;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class DSBlocks {

    public static final ModSpecificDeferredRegister<Block> BLOCKS = ModSpecificDeferredRegister.create(ForgeRegistries.BLOCKS, DoubleSlabs.MODID);

    public static final RegistryObject<DynamicSlabBlock> DOUBLE_SLAB = BLOCKS.register("double_slab", DoubleSlabBlock::new);
    public static final RegistryObject<DynamicSlabBlock> VERTICAL_SLAB = BLOCKS.register("vertical_slab", VerticalSlabBlock::new);

    public static final RegistryObject<RaisedCampfireBlock> RAISED_CAMPFIRE = BLOCKS.register("raised_campfire", () -> new RaisedCampfireBlock(Blocks.CAMPFIRE, 1, Block.Properties.from(Blocks.CAMPFIRE)));
    public static final RegistryObject<RaisedCampfireBlock> RAISED_SOUL_CAMPFIRE = BLOCKS.register("raised_soul_campfire", () -> new RaisedCampfireBlock(new ResourceLocation("extendednether:soul_campfire"), 2, Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD).lightValue(10).notSolid()), "extendednether");

}
