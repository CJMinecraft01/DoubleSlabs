package cjminecraft.doubleslabs.neoforge.common.block.entity;

import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.library.state.SlabStateContainer;
import cjminecraft.doubleslabs.neoforge.client.model.NeoForgeDynamicSlabBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class NeoForgeDynamicSlabBlockEntity extends DynamicSlabBlockEntity<SlabStateContainer> {
    public NeoForgeDynamicSlabBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    protected SlabStateContainer createBlockStateContainer() {
        return new SlabStateContainer(this, worldPosition);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        requestModelDataUpdate();
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(NeoForgeDynamicSlabBakedModel.DYNAMIC_SLAB_STATE_CONTAINER, this).build();
    }
}
