package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static cjminecraft.doubleslabs.common.Constants.id;
import static cjminecraft.doubleslabs.common.init.DSItems.VERTICAL_SLAB;

public class DSCreativeTabs {

    public static final ResourceLocation VERTICAL_SLABS_TAB_ID = id("vertical_slabs");

    public static CreativeModeTab createVerticalSlabsTab(CreativeModeTab.Builder builder, Iterable<Item> items) {
        return builder
                .title(Component.translatable("item_group." + Constants.MOD_ID + ".vertical_slabs"))
                .icon(() -> VerticalSlabItem.setContainedSlabItem(VERTICAL_SLAB.get().getDefaultInstance(), new ItemStack(Items.SMOOTH_STONE_SLAB)))
                .displayItems((params, output) -> items.forEach(item -> {
                    final var helper = Internal.getSlabHelper().getHorizontalSlabHelper(item);

                    if (helper.isEmpty()) {
                        return;
                    }

                    output.accept(VerticalSlabItem.setContainedSlabItem(VERTICAL_SLAB.get().getDefaultInstance(), new ItemStack(item)));
                }))
                .build();
    }

}
