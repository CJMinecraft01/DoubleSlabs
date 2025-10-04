package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.library.helpers.VerticalSlabModelHelper;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class VerticalSlabModelBaker {

    private final Map<Block, Map<Direction, BakedModel>> verticalModels = new HashMap<>();
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
                bakeVariants(block);
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

    private void bakeVariants(final Block block) {
		final var directionalModels = Maps.<Direction, BakedModel>newEnumMap(Direction.class);
		var resourceLocation = BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/");
		directionalModels.put(Direction.NORTH, bakeVariant(resourceLocation, Direction.NORTH));
		directionalModels.put(Direction.SOUTH, bakeVariant(resourceLocation, Direction.SOUTH));
		directionalModels.put(Direction.EAST,  bakeVariant(resourceLocation, Direction.EAST));
		directionalModels.put(Direction.WEST,  bakeVariant(resourceLocation, Direction.WEST));
		verticalModels.put(block, directionalModels);
    }
	
	private @Nullable BakedModel bakeVariant(final ResourceLocation resourceLocation, final Direction direction) {
		//final var resourceLocationVertical = resourceLocation.withSuffix("_vertical_" + direction.toString());
		final var resourceLocationHorizontal = (direction == Direction.SOUTH || direction == Direction.EAST) ? resourceLocation.withSuffix("_top") : resourceLocation;
		final var rotation = switch (direction) {
			case Direction.NORTH, Direction.SOUTH -> BlockModelRotation.X90_Y180;
			case Direction.WEST, Direction.EAST -> BlockModelRotation.X90_Y90;
			default -> BlockModelRotation.X0_Y0;
		};
		return modelBaker.bake(resourceLocationHorizontal, rotation);
	}
}
