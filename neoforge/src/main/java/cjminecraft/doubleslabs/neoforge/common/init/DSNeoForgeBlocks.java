package cjminecraft.doubleslabs.neoforge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.neoforge.common.block.NeoForgeMixedDoubleSlabBlock;
import cjminecraft.doubleslabs.neoforge.common.block.NeoForgeVerticalSlabBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static cjminecraft.doubleslabs.common.init.DSBlocks.*;

public class DSNeoForgeBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    public static final DeferredHolder<Block, MixedDoubleSlabBlock> MIXED_SLAB = BLOCKS.register(MIXED_SLAB_ID.getPath(), () -> new NeoForgeMixedDoubleSlabBlock(MIXED_SLAB_PROPERTIES));
    public static final DeferredHolder<Block, MixedDoubleSlabBlock> TRANSPARENT_MIXED_SLAB = BLOCKS.register(TRANSPARENT_MIXED_SLAB_ID.getPath(), () -> new NeoForgeMixedDoubleSlabBlock(TRANSPARENT_MIXED_SLAB_PROPERTIES));
    public static final DeferredHolder<Block, VerticalSlabBlock> VERTICAL_SLAB = BLOCKS.register(VERTICAL_SLAB_ID.getPath(), () -> new NeoForgeVerticalSlabBlock(VERTICAL_SLAB_PROPERTIES));

}
