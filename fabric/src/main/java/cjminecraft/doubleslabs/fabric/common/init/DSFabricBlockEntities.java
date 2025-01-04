package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.fabric.common.block.entity.FabricDynamicSlabBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

import static cjminecraft.doubleslabs.common.init.DSBlockEntities.*;

public class DSFabricBlockEntities {

    public static final BlockEntityType<? extends IDynamicSlabStateContainer> DYNAMIC_SLAB = new BlockEntityType<>(FabricDynamicSlabBlockEntity::new, Set.of(DSFabricBlocks.MIXED_SLAB));

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, DYNAMIC_SLAB_ID, DYNAMIC_SLAB);
    }

}
