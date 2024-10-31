package cjminecraft.doubleslabs.forge.common.platform;

import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.common.platform.services.IPlatformBlockEntityFactory;
import cjminecraft.doubleslabs.forge.common.block.entity.ForgeDynamicSlabBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ForgeBlockEntityFactory implements IPlatformBlockEntityFactory {
    @Override
    public DynamicSlabBlockEntity<? extends ISlabStateContainer> createDynamicSlabBlockEntity(BlockPos pos, BlockState state) {
        return new ForgeDynamicSlabBlockEntity(pos, state);
    }
}
