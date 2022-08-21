package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.common.init.ITabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class DSTabs implements ITabs {

    public static final DSTabs INSTANCE = new DSTabs();

    public static final CreativeModeTab VERTICAL_SLABS = new CreativeModeTab("vertical_slabs") {
        @Override
        public @NotNull ItemStack makeIcon() {
            ItemStack stack = new ItemStack(DSItems.VERTICAL_SLAB.get());
            stack.addTagElement("item", Items.STONE_BRICK_SLAB.getDefaultInstance().serializeNBT());
            return stack;
        }
    };

    @Override
    public CreativeModeTab getVerticalSlabsTab() {
        return VERTICAL_SLABS;
    }
}
