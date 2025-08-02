package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.fabric.common.block.entity.FabricDynamicSlabBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static cjminecraft.doubleslabs.common.init.DSBlockEntities.*;

public class DSFabricBlockEntities {

    public static final BlockEntityType<? extends IDynamicSlabStateContainer> DYNAMIC_SLAB =
            BlockEntityType.Builder.of(FabricDynamicSlabBlockEntity::new,
                    DSFabricBlocks.MIXED_SLAB, DSFabricBlocks.TRANSPARENT_MIXED_SLAB, DSFabricBlocks.VERTICAL_SLAB)
                    .build(null);

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, DYNAMIC_SLAB_ID, DYNAMIC_SLAB);
    }

}
