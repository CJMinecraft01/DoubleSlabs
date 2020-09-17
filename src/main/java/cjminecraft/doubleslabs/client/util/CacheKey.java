package cjminecraft.doubleslabs.client.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;

import java.util.Objects;

public class CacheKey {

    protected final IBlockState state;
    protected final EnumFacing side;
    protected final long random;
    protected final BlockRenderLayer renderLayer;

    public CacheKey(IBlockState state, EnumFacing side, long random) {
        this.state = state;
        this.side = side;
        this.random = random;
        this.renderLayer = MinecraftForgeClient.getRenderLayer();
    }

    public IBlockState getState() {
        return this.state;
    }

    public EnumFacing getSide() {
        return this.side;
    }

    public long getRandom() {
        return this.random;
    }

    public BlockRenderLayer getRenderLayer() {
        return this.renderLayer;
    }

    @Override
    public String toString() {
        return "CacheKey{" +
                "state=" + state +
                ", side=" + side +
                ", random=" + random +
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
                random == cacheKey.random &&
                Objects.equals(renderLayer, cacheKey.renderLayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, side, random, renderLayer);
    }
}
