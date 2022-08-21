package cjminecraft.doubleslabs.fabric.common.item;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FabricVerticalSlabItem extends VerticalSlabItem {

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab tab, @NotNull NonNullList<ItemStack> items) {
        if (this.allowedIn(tab)) {
            Registry.ITEM.forEach(item -> {
                if (SlabSupport.isHorizontalSlab(item)) {
                    ItemStack stack = new ItemStack(this);
                    items.add(setStack(stack, item.getDefaultInstance()));
                }
            });
        }
    }
}
