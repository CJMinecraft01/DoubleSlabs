package cjminecraft.doubleslabs.neoforge.platform;

import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntityFactory;
import cjminecraft.doubleslabs.neoforge.block.entity.NeoForgeDynamicSlabBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class NeoForgeBlockEntityFactory implements IPlatformBlockEntityFactory {
    @Override
    public DynamicSlabBlockEntity<? extends ISlabStateContainer> createDynamicSlabBlockEntity(BlockPos pos, BlockState state) {
        return new NeoForgeDynamicSlabBlockEntity(pos, state);
    }
}
