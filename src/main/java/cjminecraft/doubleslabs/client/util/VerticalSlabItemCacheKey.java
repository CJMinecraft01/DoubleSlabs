package cjminecraft.doubleslabs.client.util;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.Objects;

public class VerticalSlabItemCacheKey extends CacheKey {

    private final ItemStack stack;
    private final IBakedModel model;

    public VerticalSlabItemCacheKey(EnumFacing side, long random, ItemStack stack, IBakedModel model) {
        super(null, side, random);
        this.stack = stack;
        this.model = model;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public IBakedModel getModel() {
        return this.model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VerticalSlabItemCacheKey that = (VerticalSlabItemCacheKey) o;
        return stack.equals(that.stack) &&
                model.equals(that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stack, model);
    }
}
