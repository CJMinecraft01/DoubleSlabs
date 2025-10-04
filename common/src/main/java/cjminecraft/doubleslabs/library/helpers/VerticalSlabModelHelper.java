package cjminecraft.doubleslabs.library.helpers;

import cjminecraft.doubleslabs.api.helpers.IVerticalSlabModelHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class VerticalSlabModelHelper implements IVerticalSlabModelHelper {

    private final Map<Block, Map<Direction, BakedModel>> verticalModels;
    private final Map<Item, BakedModel> itemModels;

    public VerticalSlabModelHelper(Map<Block, Map<Direction, BakedModel>> verticalModels, Map<Item, BakedModel> itemModels) {
        this.verticalModels = verticalModels;
        this.itemModels = itemModels;
    }

    @Override
    public BakedModel getVerticalSlabModel(BlockState state, Direction side) {
        return verticalModels.get(state.getBlock()).get(side);
    }

    @Override
    public BakedModel getVerticalSlabModel(Item item) {
        return itemModels.get(item);
    }
}
