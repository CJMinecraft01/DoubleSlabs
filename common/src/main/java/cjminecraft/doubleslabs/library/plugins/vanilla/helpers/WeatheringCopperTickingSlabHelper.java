package cjminecraft.doubleslabs.library.plugins.vanilla.helpers;

import cjminecraft.doubleslabs.api.helpers.ITickingSlabHelper;
import cjminecraft.doubleslabs.api.state.ISlabStateContainer;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperTickingSlabHelper implements ITickingSlabHelper {
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public void randomTick(ISlabStateContainer stateContainer, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState originalState = stateContainer.getBlockState();

        Preconditions.checkState(originalState.getBlock() instanceof WeatheringCopperSlabBlock);

        WeatheringCopperSlabBlock block = (WeatheringCopperSlabBlock) originalState.getBlock();

        if (random.nextFloat() < 0.05688889F) {
            block.getNextState(originalState, level, pos, random).ifPresent(stateContainer::setBlockState);
        }
    }
}
