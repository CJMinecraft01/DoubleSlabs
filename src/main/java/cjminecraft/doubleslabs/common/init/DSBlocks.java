package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.util.ModSpecificDeferredRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class DSBlocks {

    public static final ModSpecificDeferredRegister<Block> BLOCKS = ModSpecificDeferredRegister.create(ForgeRegistries.BLOCKS, DoubleSlabs.MODID);

    public static final RegistryObject<DynamicSlabBlock> DOUBLE_SLAB = BLOCKS.register("double_slab", DoubleSlabBlock::new);
    public static final RegistryObject<DynamicSlabBlock> VERTICAL_SLAB = BLOCKS.register("vertical_slab", VerticalSlabBlock::new);

    public static final RegistryObject<RaisedCampfireBlock> RAISED_CAMPFIRE = BLOCKS.register("raised_campfire", () -> new RaisedCampfireBlock(Blocks.CAMPFIRE, true, 1, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0F).sound(SoundType.WOOD).lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0).noOcclusion()));
    public static final RegistryObject<RaisedCampfireBlock> RAISED_SOUL_CAMPFIRE = BLOCKS.register("raised_soul_campfire", () -> new RaisedCampfireBlock(Blocks.SOUL_CAMPFIRE, false, 2, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0F).sound(SoundType.WOOD).lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 10 : 0).noOcclusion()));

}
