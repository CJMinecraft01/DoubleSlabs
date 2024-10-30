package cjminecraft.doubleslabs.api.helpers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ISlabHelper {

    @Nullable
    IHorizontalSlabHelper getHorizontalSlabHelper(BlockState state);

    @Nullable
    IHorizontalSlabHelper getHorizontalSlabHelper(ItemStack stack);

    boolean isHorizontalSlab(Item item);

    boolean isHorizontalSlab(Block block);

}
