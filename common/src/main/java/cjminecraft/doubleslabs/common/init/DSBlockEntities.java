package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.platform.Services;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DSBlockEntities {

    public static final BlockEntityType<? extends IDynamicSlabStateContainer> DYNAMIC_SLAB = BlockEntityType.Builder.of(Services.PLATFORM.getBlockEntityFactory()::createDynamicSlabBlockEntity, DSBlocks.MIXED_SLAB).build(null);

}
