package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.forge.common.block.ForgeMixedDoubleSlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cjminecraft.doubleslabs.common.init.DSBlocks.*;

public class DSForgeBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    public static final RegistryObject<MixedDoubleSlabBlock> MIXED_SLAB = BLOCKS.register(MIXED_SLAB_ID.getPath(), () -> new ForgeMixedDoubleSlabBlock(MIXED_SLAB_PROPERTIES));
    public static final RegistryObject<MixedDoubleSlabBlock> TRANSPARENT_MIXED_SLAB = BLOCKS.register(TRANSPARENT_MIXED_SLAB_ID.getPath(), () -> new ForgeMixedDoubleSlabBlock(TRANSPARENT_MIXED_SLAB_PROPERTIES));

}
