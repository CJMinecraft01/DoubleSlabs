package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

import static cjminecraft.doubleslabs.common.Constants.id;

public class DSBlockEntities {

    public static final ResourceLocation DYNAMIC_SLAB_ID = id("dynamic_slab");

    public static final BlockEntityType<? extends IDynamicSlabStateContainer> DYNAMIC_SLAB = new BlockEntityType<>(Services.PLATFORM.getBlockEntityFactory()::createDynamicSlabBlockEntity, Set.of(DSBlocks.MIXED_SLAB));

}
