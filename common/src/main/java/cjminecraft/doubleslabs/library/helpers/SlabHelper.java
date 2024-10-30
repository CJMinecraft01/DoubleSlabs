package cjminecraft.doubleslabs.library.helpers;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.helpers.ISlabHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlabHelper implements ISlabHelper {

    private final List<IHorizontalSlabHelper> horizontalSlabHelpers = new ArrayList<>();

    public void addHorizontalSlabSupport(IHorizontalSlabHelper helper) {
        horizontalSlabHelpers.add(helper);
    }

    public Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(BlockState state) {
        if (state.getBlock() instanceof IHorizontalSlabHelper helper && helper.isHorizontalSlab(state)) {
            return Optional.of(helper);
        }

        return horizontalSlabHelpers.stream().filter(helper -> helper.isHorizontalSlab(state)).findFirst();
    }

    public Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(ItemStack stack) {
        if (stack.getItem() instanceof IHorizontalSlabHelper helper && helper.isHorizontalSlab(stack)) {
            return Optional.of(helper);
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IHorizontalSlabHelper support && support.isHorizontalSlab(stack)) {
            return Optional.of(support);
        }

        return horizontalSlabHelpers.stream().filter(helper -> helper.isHorizontalSlab(stack)).findFirst();
    }

    @Override
    public Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(Block block) {
        return horizontalSlabHelpers.stream().filter(helper -> helper.isHorizontalSlab(block)).findFirst();
    }

    @Override
    public Optional<IHorizontalSlabHelper> getHorizontalSlabHelper(Item item) {
        return horizontalSlabHelpers.stream().filter(helper -> helper.isHorizontalSlab(item)).findFirst();
    }

    @Override
    public boolean areSameTypeOfSlab(BlockState state, IHorizontalSlabHelper stateSlabHelper, ItemStack stack, IHorizontalSlabHelper stackSlabHelper) {
        return stateSlabHelper == stackSlabHelper && stackSlabHelper.areSameTypeOfSlab(state, stack);
    }
}
