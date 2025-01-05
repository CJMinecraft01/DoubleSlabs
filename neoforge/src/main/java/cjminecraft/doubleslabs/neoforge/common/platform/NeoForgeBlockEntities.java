package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntities;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class NeoForgeBlockEntities implements IPlatformBlockEntities {
    @Override
    public Supplier<BlockEntityType<? extends IDynamicSlabStateContainer>> getDynamicSlabBlockEntity() {
        return DSNeoForgeBlockEntities.DYNAMIC_SLAB_BLOCK_ENTITY;
    }
}
