package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.init.ITabs;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DSTabs implements ITabs {

    public static final DSTabs INSTANCE = new DSTabs();

    public static final CreativeModeTab VERTICAL_SLABS = FabricItemGroupBuilder.build(
            new ResourceLocation(Constants.MODID, "vertical_slabs"),
            () -> {
                ItemStack stack = new ItemStack(DSItems.VERTICAL_SLAB);
                stack.addTagElement("item", Items.STONE_BRICK_SLAB.getDefaultInstance().save(new CompoundTag()));
                return stack;
            }
    );

    @Override
    public CreativeModeTab getVerticalSlabsTab() {
        return VERTICAL_SLABS;
    }
}
