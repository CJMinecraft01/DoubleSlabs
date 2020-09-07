package cjminecraft.doubleslabs.client.util;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

public class VerticalSlabItemCacheKey extends CacheKey {

    private final ItemStack stack;
    private final IBakedModel model;

    public VerticalSlabItemCacheKey(Direction side, Random random, ItemStack stack, IBakedModel model) {
        super(null, side, random, EmptyModelData.INSTANCE);
        this.stack = stack;
        this.model = model;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public IBakedModel getModel() {
        return this.model;
    }
}
