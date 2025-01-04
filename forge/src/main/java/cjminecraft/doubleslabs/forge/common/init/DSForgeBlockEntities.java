package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.forge.common.block.entity.ForgeDynamicSlabBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

import static cjminecraft.doubleslabs.common.init.DSBlockEntities.*;

public class DSForgeBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<? extends IDynamicSlabStateContainer>> DYNAMIC_SLAB = BLOCK_ENTITY_TYPES.register(DYNAMIC_SLAB_ID.getPath(), () -> new BlockEntityType<>(ForgeDynamicSlabBlockEntity::new, Set.of(DSForgeBlocks.MIXED_SLAB.get())));

}
