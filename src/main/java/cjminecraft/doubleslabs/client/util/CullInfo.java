package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.api.IBlockInfo;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

import java.util.Objects;

public class CullInfo {

    private final IBlockInfo positiveBlock;
    private final IBlockInfo negativeBlock;
    private final BlockState state;
    private final BlockState otherState;
    private final Direction direction;

    public CullInfo(IBlockInfo positiveBlock, IBlockInfo negativeBlock, BlockState state, BlockState otherState, Direction direction) {
        this.positiveBlock = positiveBlock;
        this.negativeBlock = negativeBlock;
        this.state = state;
        this.otherState = otherState;
        this.direction = direction;
    }

    public IBlockInfo getPositiveBlock() {
        return this.positiveBlock;
    }

    public IBlockInfo getNegativeBlock() {
        return this.negativeBlock;
    }

    public BlockState getState() {
        return this.state;
    }

    public BlockState getOtherState() {
        return this.otherState;
    }

    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CullInfo cullInfo = (CullInfo) o;
        return positiveBlock.equals(cullInfo.positiveBlock) &&
                negativeBlock.equals(cullInfo.negativeBlock) &&
                state.equals(cullInfo.state) &&
                otherState.equals(cullInfo.otherState) &&
                direction == cullInfo.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positiveBlock, negativeBlock, state, otherState, direction);
    }
}
