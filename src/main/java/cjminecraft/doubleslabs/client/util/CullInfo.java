package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import java.util.Objects;

public class CullInfo implements IStateContainer {

    private final IBlockInfo positiveBlock;
    private final IBlockInfo negativeBlock;
    private final IBlockState state;
    private final IBlockState otherState;
    private final EnumFacing direction;

    public CullInfo(IBlockInfo positiveBlock, IBlockInfo negativeBlock, IBlockState state, IBlockState otherState, EnumFacing direction) {
        this.positiveBlock = positiveBlock;
        this.negativeBlock = negativeBlock;
        this.state = state;
        this.otherState = otherState;
        this.direction = direction;
    }

    @Override
    public IBlockInfo getPositiveBlockInfo() {
        return this.positiveBlock;
    }

    @Override
    public IBlockInfo getNegativeBlockInfo() {
        return this.negativeBlock;
    }

    public IBlockState getState() {
        return this.state;
    }

    public IBlockState getOtherState() {
        return this.otherState;
    }

    public EnumFacing getDirection() {
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
