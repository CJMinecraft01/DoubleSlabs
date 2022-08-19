package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockEntities {

    BlockEntityType<? extends SlabBlockEntity<?>> dynamicSlab();

    SlabBlockEntity<?> createSlabBlockEntity(BlockPos pos, BlockState state);

}
