package cjminecraft.doubleslabs.api.support.mubble;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SlabSupportProvider(modid = "mubble")
public class MubbleSlabSupport<T extends Enum<T> & IStringSerializable> extends VerticalSlabSupport<T> {

    public MubbleSlabSupport() {
        super("hugman.mubble.objects.block.VerticalSlabBlock", "TYPE", "hugman.mubble.objects.block.block_state_property.VerticalSlabType");
    }

    @Override
    public BlockState getStateForDirection(World world, BlockPos pos, BlockState state, Direction direction) {
        if (verticalSlab == null)
            return state;
        if (direction == Direction.SOUTH)
            return state.with(verticalSlabTypeProperty, verticalSlabTypes[0]);
        else if (direction == Direction.NORTH)
            return state.with(verticalSlabTypeProperty, verticalSlabTypes[1]);
        else if (direction == Direction.WEST)
            return state.with(verticalSlabTypeProperty, verticalSlabTypes[2]);
        else
            return state.with(verticalSlabTypeProperty, verticalSlabTypes[3]);
    }

    @Override
    public Direction getDirection(World world, BlockPos pos, BlockState state) {
        if (verticalSlab == null)
            return super.getDirection(world, pos, state);
        Enum<T> type = state.get(verticalSlabTypeProperty);
        if (type.equals(verticalSlabTypes[0]))
            return Direction.SOUTH;
        else if (type.equals(verticalSlabTypes[1]))
            return Direction.NORTH;
        else if (type.equals(verticalSlabTypes[2]))
            return Direction.WEST;
        return Direction.EAST;
    }
}

