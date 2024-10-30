package cjminecraft.doubleslabs.api.helpers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface ISlabHelper {

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(BlockState state);

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(ItemStack stack);

    boolean isHorizontalSlab(Item item);

    boolean isHorizontalSlab(Block block);

}
