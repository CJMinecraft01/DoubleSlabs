package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntityFactory;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ForgeBlockEntityFactory implements IPlatformBlockEntityFactory {
    @Override
    public Supplier<BlockEntityType<? extends IDynamicSlabStateContainer>> getDynamicSlabBlockEntity() {
        return DSForgeBlockEntities.DYNAMIC_SLAB;
    }
}
