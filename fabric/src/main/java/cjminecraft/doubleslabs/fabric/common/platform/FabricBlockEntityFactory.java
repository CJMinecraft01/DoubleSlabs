package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntityFactory;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class FabricBlockEntityFactory implements IPlatformBlockEntityFactory {
    @Override
    public Supplier<BlockEntityType<? extends IDynamicSlabStateContainer>> getDynamicSlabBlockEntity() {
        return () -> DSFabricBlockEntities.DYNAMIC_SLAB;
    }
}
