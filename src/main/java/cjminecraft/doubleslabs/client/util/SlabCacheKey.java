package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SlabCacheKey extends CacheKey implements IStateContainer {

    private final IBlockInfo positiveBlock;
    private final IBlockInfo negativeBlock;
    private final List<CullInfo> cullInfo;

    public SlabCacheKey(IBlockInfo positiveBlock, IBlockInfo negativeBlock, Direction side, Random random, List<CullInfo> cullInfo, IModelData modelData, BlockState state) {
        super(state, side, random, modelData);
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

    public Direction getSide() {
        return this.side;
    }

    public BlockRenderLayer getRenderLayer() {
        return this.renderLayer;
    }

    public Random getRandom() {
        return this.random;
    }

    public List<CullInfo> getCullInfo() {
        return this.cullInfo;
    }

    public IModelData getModelData() {
        return this.modelData;
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
                ", modelData=" + modelData +
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
