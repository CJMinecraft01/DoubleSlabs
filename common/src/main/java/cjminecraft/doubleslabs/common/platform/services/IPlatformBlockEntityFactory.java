package cjminecraft.doubleslabs.common.platform.services;

import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IPlatformBlockEntityFactory {

    DynamicSlabBlockEntity<? extends ISlabStateContainer> createDynamicSlabBlockEntity(BlockPos pos, BlockState state);

}
