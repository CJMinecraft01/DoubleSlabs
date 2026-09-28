package cjminecraft.doubleslabs.neoforge.compat.quark;

import cjminecraft.doubleslabs.api.helpers.IVerticalSlabHelper;
import cjminecraft.doubleslabs.api.state.VerticalSlabState;
import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.quark.content.building.block.VerticalSlabBlock;

public class QuarkVerticalSlabHelper implements IVerticalSlabHelper {
    @Override
    public boolean isVerticalSlab(Block block) {
        // Stone Zone and other integrations instantiate this same base class.
        return block instanceof VerticalSlabBlock;
    }

    @Override
    public VerticalSlabState getVerticalSlabState(BlockGetter level, BlockPos pos, BlockState state) {
        final var type = state.getValue(VerticalSlabBlock.TYPE);
        if (type == VerticalSlabBlock.VerticalSlabType.DOUBLE) {
            // A full Quark slab has no axis; callers reject DOUBLE before using it.
            return new VerticalSlabState(Direction.Axis.X, VerticalSlabType.DOUBLE);
        }

        // Quark names the exposed face, whereas DoubleSlabs names the occupied half.
        final var occupiedDirection = type.direction.getOpposite();
        return new VerticalSlabState(occupiedDirection.getAxis(),
                VerticalSlabType.fromAxisDirection(occupiedDirection.getAxisDirection()));
    }

    @Override
    public BlockState getStateForVerticalSlabState(BlockGetter level, BlockPos pos, BlockState state,
                                                  VerticalSlabState verticalSlabState) {
        final var type = verticalSlabState.isDouble() ? VerticalSlabBlock.VerticalSlabType.DOUBLE :
                VerticalSlabBlock.VerticalSlabType.fromDirection(verticalSlabState.getFacingDirection().getOpposite());
        return state.setValue(VerticalSlabBlock.TYPE, type);
    }
}
