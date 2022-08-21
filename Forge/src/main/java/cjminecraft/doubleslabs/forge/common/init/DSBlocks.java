package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.block.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.IBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSBlocks implements IBlocks {

    public static final DSBlocks INSTANCE = new DSBlocks();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MODID);

    public static final RegistryObject<DynamicSlabBlock> DOUBLE_SLAB = BLOCKS.register("double_slab", DoubleSlabBlock::new);
    public static final RegistryObject<DynamicSlabBlock> VERTICAL_SLAB = BLOCKS.register("vertical_slab", VerticalSlabBlock::new);

    @Override
    public DynamicSlabBlock getDoubleSlabBlock() {
        return DOUBLE_SLAB.get();
    }

    @Override
    public DynamicSlabBlock getVerticalSlabBlock() {
        return VERTICAL_SLAB.get();
    }

    // todo: raised campfire

}
