package cjminecraft.doubleslabs.api.support;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface IVerticalSlabSupport extends ISlabSupport {

    boolean isVerticalSlab(BlockGetter world, BlockPos pos, BlockState state);

    boolean isVerticalSlab(ItemStack stack, Player player, InteractionHand hand);

    Direction getDirection(BlockGetter world, BlockPos pos, BlockState state);

    BlockState getStateForDirection(BlockGetter world, BlockPos pos, BlockState state, Direction direction);

    default boolean rotateModel(LevelAccessor world, BlockPos pos, BlockState state) {
        return false;
    }

}
