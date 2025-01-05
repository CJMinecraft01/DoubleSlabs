package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntities;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FabricBlockEntities implements IPlatformBlockEntities {
    @Override
    public BlockEntityType<? extends IDynamicSlabStateContainer> getDynamicSlabBlockEntity() {
        return DSFabricBlockEntities.DYNAMIC_SLAB;
    }
}
