package cjminecraft.doubleslabs.library.helpers;

import cjminecraft.doubleslabs.api.helpers.IVerticalSlabModelHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Map;

public class VerticalSlabModelHelper implements IVerticalSlabModelHelper {

    private final Map<BlockState, Map<Direction, BakedModel>> verticalModels;
    private final Map<Item, BakedModel> itemModels;

    public VerticalSlabModelHelper(Map<BlockState, Map<Direction, BakedModel>> verticalModels, Map<Item, BakedModel> itemModels) {
        this.verticalModels = verticalModels;
        this.itemModels = itemModels;
    }

    @Override
    public BakedModel getVerticalSlabModel(BlockState state, Direction side) {
		final var normalisedState = state.setValue(BlockStateProperties.WATERLOGGED, false);
		return verticalModels.get(normalisedState).get(side);
    }

    @Override
    public BakedModel getVerticalSlabModel(Item item) {
        return itemModels.get(item);
    }
}
