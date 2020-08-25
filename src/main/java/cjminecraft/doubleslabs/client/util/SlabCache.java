package cjminecraft.doubleslabs.client.util;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;

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
    private final IModelData modelData;
    private final BlockState originalState;

    public SlabCache(IBlockInfo positiveBlock, IBlockInfo negativeBlock, Direction side, Random random, List<CullInfo> cullInfo, IModelData modelData, BlockState originalState) {
        this.positiveBlock = positiveBlock;
        this.negativeBlock = negativeBlock;
        this.side = side;
        this.random = random;
        this.modelData = modelData;
        this.originalState = originalState;
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

    public IModelData getModelData() {
        return this.modelData;
    }

    public BlockState getOriginalState() {
        return this.originalState;
    }

    @Override
    public String toString() {
        return "SlabCache{" +
                "positiveBlock=" + positiveBlock +
                ", negativeBlock=" + negativeBlock +
                ", side=" + side +
                ", renderLayer=" + renderLayer +
                ", random=" + random +
                ", cullInfo=" + cullInfo +
                ", modelData=" + modelData +
                ", originalState=" + originalState +
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
                cullInfo.equals(slabCache.cullInfo) &&
                originalState.equals(slabCache.originalState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positiveBlock, negativeBlock, side, renderLayer, cullInfo, originalState);
    }
}
