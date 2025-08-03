package cjminecraft.doubleslabs.forge.common.block.entity;

import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.forge.client.model.ForgeDynamicSlabBakedModel;
import cjminecraft.doubleslabs.forge.common.state.ForgeSlabStateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class ForgeDynamicSlabBlockEntity extends DynamicSlabBlockEntity<ForgeSlabStateContainer> {
    public ForgeDynamicSlabBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    protected ForgeSlabStateContainer createBlockStateContainer() {
        return new ForgeSlabStateContainer(this, worldPosition);
    }

    @Override
    public void markDirty() {
        requestModelDataUpdate();
        super.markDirty();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        final var negativeCapability = negativeBlockStateContainer.getCapability(cap, side);
        final var positiveCapability = positiveBlockStateContainer.getCapability(cap, side);
        return negativeCapability.isPresent() ? negativeCapability : positiveCapability;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        final var negativeCapability = negativeBlockStateContainer.getCapability(cap);
        final var positiveCapability = positiveBlockStateContainer.getCapability(cap);
        return negativeCapability.isPresent() ? negativeCapability : positiveCapability;
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(ForgeDynamicSlabBakedModel.DYNAMIC_SLAB_STATE_CONTAINER, this).build();
    }
}
