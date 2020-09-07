package cjminecraft.doubleslabs.client.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Objects;
import java.util.Random;

public class CacheKey {

    protected final BlockState state;
    protected final Direction side;
    protected final Random random;
    protected final IModelData modelData;
    protected final RenderType renderLayer;

    public CacheKey(BlockState state, Direction side, Random random, IModelData modelData) {
        this.state = state;
        this.side = side;
        this.random = random;
        this.modelData = modelData;
        this.renderLayer = MinecraftForgeClient.getRenderLayer();
    }

    public BlockState getState() {
        return this.state;
    }

    public Direction getSide() {
        return this.side;
    }

    public Random getRandom() {
        return this.random;
    }

    public IModelData getModelData() {
        return this.modelData;
    }

    public RenderType getRenderLayer() {
        return this.renderLayer;
    }

    @Override
    public String toString() {
        return "CacheKey{" +
                "state=" + state +
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
        CacheKey cacheKey = (CacheKey) o;
        return Objects.equals(state, cacheKey.state) &&
                side == cacheKey.side &&
                random.equals(cacheKey.random) &&
                modelData.equals(cacheKey.modelData) &&
                Objects.equals(renderLayer, cacheKey.renderLayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, side, random, modelData, renderLayer);
    }
}
