package cjminecraft.doubleslabs.api.helpers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface ISlabHelper {

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(BlockState state);

    Optional<IVerticalSlabHelper> getVerticalSlabHelper(BlockState state);

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(ItemStack stack);

    Optional<IVerticalSlabHelper> getVerticalSlabHelper(ItemStack stack);

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(Block block);

    Optional<IVerticalSlabHelper> getVerticalSlabHelper(Block block);

    Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(Item item);

    Optional<IVerticalSlabHelper> getVerticalSlabHelper(Item item);

    default boolean isHorizontalSlab(BlockState state) {
        return getHorizontalSlabHelper(state).isPresent();
    }

    default boolean isVerticalSlab(BlockState state) {
        return getVerticalSlabHelper(state).isPresent();
    }

    default boolean isHorizontalSlab(ItemStack stack) {
        return getHorizontalSlabHelper(stack).isPresent();
    }

    default boolean isVerticalSlab(ItemStack stack) {
        return getVerticalSlabHelper(stack).isPresent();
    }

    default boolean isHorizontalSlab(Item item) {
        return getHorizontalSlabHelper(item).isPresent();
    }

    default boolean isVerticalSlab(Item item) {
        return getVerticalSlabHelper(item).isPresent();
    }

    default boolean isHorizontalSlab(Block block) {
        return getHorizontalSlabHelper(block).isPresent();
    }

    default boolean isVerticalSlab(Block block) {
        return getVerticalSlabHelper(block).isPresent();
    }

    boolean areSameTypeOfSlab(BlockState state, IHorizontalSlabHelper stateSlabHelper, ItemStack stack, IHorizontalSlabHelper stackSlabHelper);

    boolean areSameTypeOfSlab(BlockState state, IVerticalSlabHelper stateSlabHelper, ItemStack stack, IVerticalSlabHelper stackSlabHelper);

    Optional<ISlabAbilities> getSlabAbilities(Block block);

}
