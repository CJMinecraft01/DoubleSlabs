package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.Objects;

public class SlabCacheKey extends CacheKey implements IStateContainer {

    private final IBlockInfo positiveBlock;
    private final IBlockInfo negativeBlock;
    private final List<CullInfo> cullInfo;

    public SlabCacheKey(IBlockInfo positiveBlock, IBlockInfo negativeBlock, EnumFacing side, long random, List<CullInfo> cullInfo, IBlockState state) {
        super(state, side, random);
        this.positiveBlock = positiveBlock;
        this.negativeBlock = negativeBlock;
        this.cullInfo = cullInfo;
    }

    @Override
    public IBlockInfo getPositiveBlockInfo() {
        return this.positiveBlock;
    }

    @Override
    public IBlockInfo getNegativeBlockInfo() {
        return this.negativeBlock;
    }

    public boolean isValid() {
        return this.positiveBlock != null && this.negativeBlock != null && this.cullInfo != null;
    }

    public List<CullInfo> getCullInfo() {
        return this.cullInfo;
    }

    @Override
    public String toString() {
        return "SlabCacheKey{" +
                "positiveBlock=" + positiveBlock +
                ", negativeBlock=" + negativeBlock +
                ", cullInfo=" + cullInfo +
                ", state=" + state +
                ", side=" + side +
                ", random=" + random +
                ", renderLayer=" + renderLayer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SlabCacheKey that = (SlabCacheKey) o;
        return Objects.equals(positiveBlock, that.positiveBlock) &&
                Objects.equals(negativeBlock, that.negativeBlock) &&
                Objects.equals(cullInfo, that.cullInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), positiveBlock, negativeBlock, cullInfo);
    }
}
