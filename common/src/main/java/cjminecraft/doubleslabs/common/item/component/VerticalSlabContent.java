package cjminecraft.doubleslabs.common.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class VerticalSlabContent {

    public static final VerticalSlabContent EMPTY = new VerticalSlabContent(ItemStack.EMPTY);

    public static final Codec<VerticalSlabContent> CODEC = ItemStack.CODEC.xmap(VerticalSlabContent::new, VerticalSlabContent::getItem);
    public static final StreamCodec<RegistryFriendlyByteBuf, VerticalSlabContent> STREAM_CODEC = ItemStack.STREAM_CODEC
            .map(VerticalSlabContent::new, VerticalSlabContent::getItem);

    private final ItemStack item;

    VerticalSlabContent(ItemStack item) {
        this.item = item;
    }

    public static VerticalSlabContent of(ItemStack slab) {
        return slab.isEmpty() ? EMPTY : new VerticalSlabContent(slab);
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isEmpty() {
        return item.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return other instanceof VerticalSlabContent otherContents &&
                    ItemStack.matches(item, otherContents.item);
        }
    }

    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(item);
    }

    @Override
    public String toString() {
        return "VerticalSlabContents" + item;
    }
}
