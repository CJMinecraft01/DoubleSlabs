package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntities;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ForgeBlockEntities implements IPlatformBlockEntities {
    @Override
    public Supplier<BlockEntityType<? extends IDynamicSlabStateContainer>> getDynamicSlabBlockEntity() {
        return DSForgeBlockEntities.DYNAMIC_SLAB;
    }
}
