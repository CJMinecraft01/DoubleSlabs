package cjminecraft.doubleslabs.api.helpers;

import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

import javax.annotation.Nullable;

public interface ISlabAbilities {

    default void scheduleTick(Level level, BlockPos pos, int delay) {
        level.scheduleTick(pos, level.getBlockState(pos).getBlock(), delay);
    }

    default boolean isRandomlyTicking(BlockState state) {
        return false;
    }

    default void randomTick(ISlabStateContainer stateContainer, ServerLevel level, BlockPos pos, RandomSource random) {

    }

    default void neighborChanged(ISlabStateContainer stateContainer, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {

    }

    default void tick(ISlabStateContainer stateContainer, ServerLevel level, BlockPos pos, RandomSource random) {

    }

}
