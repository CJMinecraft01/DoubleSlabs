package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SlabCache implements IStateContainer {

    private final IBlockInfo positiveBlock;
    private final IBlockInfo negativeBlock;
    private final Direction side;
    private final RenderType renderLayer;
    private final Random random;
    private final List<CullInfo> cullInfo;

    public SlabCache(IBlockInfo positiveBlock, IBlockInfo negativeBlock, Direction side, Random random, List<CullInfo> cullInfo) {
        this.positiveBlock = positiveBlock;
        this.negativeBlock = negativeBlock;
        this.side = side;
        this.random = random;
        this.renderLayer = MinecraftForgeClient.getRenderLayer();
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

    public Direction getSide() {
        return this.side;
    }

    public RenderType getRenderLayer() {
        return this.renderLayer;
    }

    public Random getRandom() {
        return this.random;
    }

    public List<CullInfo> getCullInfo() {
        return this.cullInfo;
    }

    @Override
    public String toString() {
        return "SlabCache{" +
                "positiveBlock=" + positiveBlock +
                ", negativeBlock=" + negativeBlock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlabCache slabCache = (SlabCache) o;
        return positiveBlock.equals(slabCache.positiveBlock) &&
                negativeBlock.equals(slabCache.negativeBlock) &&
                side == slabCache.side &&
                Objects.equals(renderLayer, slabCache.renderLayer) &&
                cullInfo.equals(slabCache.cullInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positiveBlock, negativeBlock, side, renderLayer, cullInfo);
    }
}
