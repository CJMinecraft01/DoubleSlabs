package cjminecraft.doubleslabs.fabric.platform;

import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntityFactory;
import cjminecraft.doubleslabs.fabric.block.entity.FabricDynamicSlabBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricBlockEntityFactory implements IPlatformBlockEntityFactory {
    @Override
    public DynamicSlabBlockEntity<? extends ISlabStateContainer> createDynamicSlabBlockEntity(BlockPos pos, BlockState state) {
        return new FabricDynamicSlabBlockEntity(pos, state);
    }
}
