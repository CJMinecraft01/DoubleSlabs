package cjminecraft.doubleslabs.neoforge.common.init;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.neoforge.common.block.entity.NeoForgeDynamicSlabBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static cjminecraft.doubleslabs.common.init.DSBlockEntities.*;

public class DSNeoForgeBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends IDynamicSlabStateContainer>> DYNAMIC_SLAB_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(DYNAMIC_SLAB_ID.getPath(), () -> BlockEntityType.Builder.of(NeoForgeDynamicSlabBlockEntity::new, DSNeoForgeBlocks.MIXED_SLAB.get(), DSNeoForgeBlocks.TRANSPARENT_MIXED_SLAB.get()).build(null));

}
