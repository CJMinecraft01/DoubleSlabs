package cjminecraft.doubleslabs.api.support;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public interface IVerticalSlabSupport extends ISlabSupport {

    boolean isVerticalSlab(IBlockReader world, BlockPos pos, BlockState state);

    boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand);

    Direction getDirection(World world, BlockPos pos, BlockState state);

    BlockState getStateForDirection(World world, BlockPos pos, BlockState state, Direction direction);

    default boolean rotateModel(IBlockDisplayReader world, BlockPos pos, BlockState state) {
        return false;
    }

}
