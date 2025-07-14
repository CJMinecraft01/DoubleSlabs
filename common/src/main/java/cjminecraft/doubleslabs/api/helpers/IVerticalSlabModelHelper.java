package cjminecraft.doubleslabs.api.helpers;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IVerticalSlabModelHelper {

    BakedModel getVerticalSlabModel(BlockState state, Direction side);

    BakedModel getVerticalSlabModel(Item item);

    default BakedModel getVerticalSlabModel(ItemStack stack) {
        return getVerticalSlabModel(stack.getItem());
    }

}
