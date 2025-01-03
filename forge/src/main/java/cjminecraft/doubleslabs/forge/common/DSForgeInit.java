package cjminecraft.doubleslabs.forge.common;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSForgeInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    public static final RegistryObject<MixedDoubleSlabBlock> MIXED_SLAB_BLOCK = BLOCKS.register("double_slab", () -> DSBlocks.MIXED_SLAB);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<? extends IDynamicSlabStateContainer>> DYNAMIC_SLAB_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("dynamic_slab", () -> DSBlockEntities.DYNAMIC_SLAB);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
    }

}
