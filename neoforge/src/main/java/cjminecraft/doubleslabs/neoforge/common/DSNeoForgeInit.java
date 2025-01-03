package cjminecraft.doubleslabs.neoforge.common;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DSNeoForgeInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    public static final DeferredHolder<Block, MixedDoubleSlabBlock> MIXED_SLAB_BLOCK = BLOCKS.register("double_slab", () -> DSBlocks.MIXED_SLAB);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends IDynamicSlabStateContainer>> DYNAMIC_SLAB_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("dynamic_slab", () -> DSBlockEntities.DYNAMIC_SLAB);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
    }

}
