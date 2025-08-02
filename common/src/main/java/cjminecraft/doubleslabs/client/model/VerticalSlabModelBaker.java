package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.library.helpers.VerticalSlabModelHelper;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class VerticalSlabModelBaker {

    private final Map<BlockState, Map<Direction, BakedModel>> verticalModels = new HashMap<>();
    private final Map<Item, BakedModel> itemModels = new HashMap<>();

    protected final PlatformIndependentModelBaker modelBaker;

    public VerticalSlabModelBaker(PlatformIndependentModelBaker modelBaker) {
        this.modelBaker = modelBaker;
    }

    public VerticalSlabModelHelper createModelHelper() {
        return new VerticalSlabModelHelper(verticalModels, itemModels);
    }

    public void bakeBlocks(final Iterable<Block> blocks) {
        verticalModels.clear();

        var count = 0;

        for (var block : blocks) {
            final var helper = Internal.getSlabHelper().getHorizontalSlabHelper(block);

            if (helper.isPresent()) {
                bakeVariants(block, helper.get());
                count++;
            }
        }

        Constants.LOG.info("Baked vertical slab models for {} slabs", count);
    }

    public void bakeItems(final Iterable<Item> items, final Function<Item, ResourceLocation> lookupResourceLocation) {
        itemModels.clear();

        var count = 0;

        for (var item : items) {
            final var helper = Internal.getSlabHelper().getHorizontalSlabHelper(item);

            if (helper.isPresent()) {
                bake(item, lookupResourceLocation.apply(item));
                count++;
            }
        }

        Constants.LOG.info("Baked vertical slab item models for {} slabs", count);
    }

    private void bake(final Item item, final ResourceLocation registryName) {
        final var model = modelBaker.getModel(registryName.withPrefix("item/"));

        if (!(model instanceof BlockModel blockModel)) {
            Constants.LOG.warn("Cannot bake vertical slab model for {} as it does not use a block model", registryName);
            return;
        }

        final var bakedModel = blockModel.bake(modelBaker, blockModel, modelBaker::getTextureSprite, BlockModelRotation.X90_Y0, false);

        itemModels.put(item, bakedModel);
    }

    private void bakeVariants(final Block block, final IHorizontalSlabHelper helper) {
        block.getStateDefinition().getPossibleStates().stream()
                .filter(Predicates.not(helper::isDoubleSlab)
                        .and(state -> !state.hasProperty(BlockStateProperties.WATERLOGGED)
                                || !state.getValue(BlockStateProperties.WATERLOGGED)))
                .forEach(this::bake);
    }

    private void bake(final BlockState state) {
        final var resourceLocation = BlockModelShaper.stateToModelLocation(state).id().withPrefix("block/");

        final var directionalModels = Maps.<Direction, BakedModel>newEnumMap(Direction.class);
        directionalModels.put(Direction.NORTH, modelBaker.bake(resourceLocation, BlockModelRotation.X90_Y180));
        directionalModels.put(Direction.EAST,  modelBaker.bake(resourceLocation, BlockModelRotation.X90_Y270));
        directionalModels.put(Direction.SOUTH, modelBaker.bake(resourceLocation, BlockModelRotation.X90_Y0));
        directionalModels.put(Direction.WEST,  modelBaker.bake(resourceLocation, BlockModelRotation.X90_Y90));

        verticalModels.put(state, directionalModels);
    }

}
