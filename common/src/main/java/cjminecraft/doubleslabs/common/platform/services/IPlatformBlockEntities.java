package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface IPlatformBlockEntities {

    BlockEntityType<? extends IDynamicSlabStateContainer> getDynamicSlabBlockEntity();

}
