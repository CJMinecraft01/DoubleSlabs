package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface IPlatformBlockEntityFactory {

    Supplier<BlockEntityType<? extends IDynamicSlabStateContainer>> getDynamicSlabBlockEntity();

}
