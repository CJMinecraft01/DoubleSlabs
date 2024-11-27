package cjminecraft.doubleslabs.api.helpers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface ISlabHelper {

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(BlockState state);

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(ItemStack stack);

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(Block block);

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(Item item);

    default boolean isHorizontalSlab(BlockState state) {
        return getHorizontalSlabHelper(state).isPresent();
    }

    default boolean isHorizontalSlab(ItemStack stack) {
        return getHorizontalSlabHelper(stack).isPresent();
    }

    default boolean isHorizontalSlab(Item item) {
        return getHorizontalSlabHelper(item).isPresent();
    }

    default boolean isHorizontalSlab(Block block) {
        return getHorizontalSlabHelper(block).isPresent();
    }

    boolean areSameTypeOfSlab(BlockState state, IHorizontalSlabHelper stateSlabHelper, ItemStack stack, IHorizontalSlabHelper stackSlabHelper);

    Optional<ITickingSlabHelper> getTickingSlabHelper(Block block);

}
