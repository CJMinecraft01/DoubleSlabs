package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.util.ModSpecificDeferredRegister;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class DSBlocks {

    public static final ModSpecificDeferredRegister<Block> BLOCKS = ModSpecificDeferredRegister.create(ForgeRegistries.BLOCKS, DoubleSlabs.MODID);

    public static final RegistryObject<DynamicSlabBlock> DOUBLE_SLAB = BLOCKS.register("double_slab", DoubleSlabBlock::new);
    public static final RegistryObject<DynamicSlabBlock> VERTICAL_SLAB = BLOCKS.register("vertical_slab", VerticalSlabBlock::new);

    public static final RegistryObject<RaisedCampfireBlock> RAISED_CAMPFIRE = BLOCKS.register("raised_campfire", () -> new RaisedCampfireBlock(Blocks.CAMPFIRE, true, 1, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD).setLightLevel(state -> state.get(BlockStateProperties.LIT) ? 15 : 0).notSolid()));
    public static final RegistryObject<RaisedCampfireBlock> RAISED_SOUL_CAMPFIRE = BLOCKS.register("raised_soul_campfire", () -> new RaisedCampfireBlock(Blocks.SOUL_CAMPFIRE, false, 2, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD).setLightLevel(state -> state.get(BlockStateProperties.LIT) ? 10 : 0).notSolid()));

    public static final RegistryObject<RaisedCampfireBlock> RAISED_ENDER_CAMPFIRE = BLOCKS.register("raised_ender_campfire", () -> new RaisedCampfireBlock(new ResourceLocation("endergetic:ender_campfire"), false , 3, Block.Properties.from(Blocks.CAMPFIRE)), "endergetic");
    public static final RegistryObject<RaisedCampfireBlock> RAISED_BORIC_CAMPFIRE = BLOCKS.register("raised_boric_campfire", () -> new RaisedCampfireBlock(new ResourceLocation("byg:boric_campfire"), true , 3, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD).setLightLevel(state -> state.get(BlockStateProperties.LIT) ? 14 : 0).notSolid()), "byg");
    public static final RegistryObject<RaisedCampfireBlock> RAISED_CRYPTIC_CAMPFIRE = BLOCKS.register("raised_cryptic_campfire", () -> new RaisedCampfireBlock(new ResourceLocation("byg:cryptic_campfire"), true , 4, AbstractBlock.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(2.0F).sound(SoundType.WOOD).setLightLevel(state -> state.get(BlockStateProperties.LIT) ? 14 : 0).notSolid()), "byg");
    public static final RegistryObject<RaisedCampfireBlock> RAISED_GLOW_CAMPFIRE = BLOCKS.register("raised_glow_campfire", () -> new RaisedCampfireBlock(new ResourceLocation("infernalexp:glow_campfire"), true , 2, Block.Properties.from(Blocks.CAMPFIRE)), "infernalexp");

}
