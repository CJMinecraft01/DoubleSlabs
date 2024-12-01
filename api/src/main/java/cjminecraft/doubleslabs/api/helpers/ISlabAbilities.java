package cjminecraft.doubleslabs.api.helpers;

import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ISlabAbilities {

    default boolean isRandomlyTicking(BlockState state) {
        return false;
    }

    default void randomTick(ISlabStateContainer stateContainer, ServerLevel level, BlockPos pos, RandomSource random) {

    }

    default void neighborChanged(ISlabStateContainer stateContainer, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {

    }

}
