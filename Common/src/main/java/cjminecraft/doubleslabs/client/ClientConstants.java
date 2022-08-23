package cjminecraft.doubleslabs.client;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.platform.ClientServices;
import cjminecraft.doubleslabs.platform.Services;
import com.google.common.collect.Maps;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Objects;

public class ClientConstants {

    private static final Map<BlockState, Map<Direction, BakedModel>> VERTICAL_SLAB_MODELS = Maps.newIdentityHashMap();
    private static final Map<Item, BakedModel> VERTICAL_SLAB_ITEM_MODELS = Maps.newIdentityHashMap();
    private static final Transformation TRANSFORMATION_2D = new Transformation(null, Vector3f.ZN.rotationDegrees(90), null, null);
    public static final int TINT_OFFSET = 1000;
    public static final Transformation RAISED_CAMPFIRE_TRANSFORM = new Transformation(new Vector3f(0, 0.5f, 0), null, null, null);

    public static boolean isTransparent(BlockState state) {
        return !state.getMaterial().isSolid() || !state.canOcclude();
    }

    public static BakedModel getFallbackModel() {
        return Minecraft.getInstance().getModelManager().getMissingModel();
    }

    public static BakedModel getVerticalModel(BlockState state, Direction direction) {
        Map<Direction, BakedModel> map = VERTICAL_SLAB_MODELS.get(state);
        if (map != null)
            return map.getOrDefault(direction, getFallbackModel());
        return getFallbackModel();
    }

    public static BakedModel getVerticalModel(Item item) {
        return VERTICAL_SLAB_ITEM_MODELS.getOrDefault(item, getFallbackModel());
    }

    public static BakedModel bake(ModelBakery modelLoader, UnbakedModel baseModel, ResourceLocation location, boolean uvlock, ModelState transform) {
        if (baseModel instanceof MultiVariant model) {
            if (model.getVariants().isEmpty()) {
                return null;
            } else {
                WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();

                for(Variant variant : model.getVariants()) {
//                    IBakedModel ibakedmodel = modelLoader.getBakedModel(variant.getModelLocation(), new ModelTransformComposition(variant, transform, uvlock), modelLoader.getSpriteMap()::getSprite);
                    BakedModel ibakedmodel = modelLoader.bake(variant.getModelLocation(), transform);
                    builder.add(ibakedmodel, variant.getWeight());
                }

                return builder.build();
            }
        } else {
            return baseModel.bake(modelLoader, ClientServices.INSTANCE.getAtlasSet(modelLoader)::getSprite, transform, location);
        }
    }

    public static void bakeVerticalSlabModels(ModelBakery modelLoader) {
        VERTICAL_SLAB_MODELS.clear();
        VERTICAL_SLAB_ITEM_MODELS.clear();

        Services.REGISTRIES.getAllBlocks().forEach(block -> {
            ISlabSupport support = SlabSupport.getHorizontalSlabSupport(block);
            if (support != null) {
//                boolean raisedCampfire = block instanceof RaisedCampfireBlock;
                boolean raisedCampfire = false;
                boolean uvlock = support.uvlock(block) && DSConfig.CLIENT.uvlock(block);
                block.getStateDefinition().getPossibleStates().forEach(state -> {
                    ModelResourceLocation resourceLocation = BlockModelShaper.stateToModelLocation(state);
                    try {
                        Map<Direction, BakedModel> map = Maps.newEnumMap(Direction.class);
                        UnbakedModel model = modelLoader.getModel(resourceLocation);
                        if (raisedCampfire) {
                            map.put(Direction.NORTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, BlockModelRotation.X90_Y180.getRotation().compose(RAISED_CAMPFIRE_TRANSFORM), uvlock, 1)));
                            map.put(Direction.EAST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, BlockModelRotation.X90_Y270.getRotation().compose(RAISED_CAMPFIRE_TRANSFORM), uvlock, 1)));
                            map.put(Direction.SOUTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, BlockModelRotation.X90_Y0.getRotation().compose(RAISED_CAMPFIRE_TRANSFORM), uvlock, 1)));
                            map.put(Direction.WEST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, BlockModelRotation.X90_Y90.getRotation().compose(RAISED_CAMPFIRE_TRANSFORM), uvlock, 1)));
                        } else {
                            map.put(Direction.NORTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, BlockModelRotation.X90_Y180.getRotation(), uvlock, 1)));
                            map.put(Direction.EAST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, BlockModelRotation.X90_Y270.getRotation(), uvlock, 1)));
                            map.put(Direction.SOUTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, BlockModelRotation.X90_Y0.getRotation(), uvlock, 1)));
                            map.put(Direction.WEST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, BlockModelRotation.X90_Y90.getRotation(), uvlock, 1)));
                        }
                        VERTICAL_SLAB_MODELS.put(state, map);
                    } catch (Exception e) {
                        Constants.LOG.warn("Failed to generate vertical slab model for: {}", resourceLocation.toString());
                        Constants.LOG.error(e.getMessage());
                    }
                });
            }
        });


        Services.REGISTRIES.getAllItems().forEach(item -> {
            if (SlabSupport.isHorizontalSlab(item)) {
                boolean uvlock = item instanceof BlockItem ? DSConfig.CLIENT.uvlock(((BlockItem) item).getBlock()) : DSConfig.CLIENT.uvlock(item);
                ModelResourceLocation resourceLocation = new ModelResourceLocation(Objects.requireNonNull(Services.REGISTRIES.getKey(item)), "inventory");
                try {
                    UnbakedModel model = modelLoader.getModel(resourceLocation);
                    boolean is3d = (model instanceof BlockModel) && ((BlockModel) model).getGuiLight().lightLikeBlock();
                    if (is3d) {
                        VERTICAL_SLAB_ITEM_MODELS.put(item, bake(modelLoader, model, resourceLocation, uvlock,
                                new Variant(resourceLocation, BlockModelRotation.X90_Y0.getRotation(), uvlock, 1)));
                    } else {
                        // Rotate 90 for 2d model
                        VERTICAL_SLAB_ITEM_MODELS.put(item, bake(modelLoader, model, resourceLocation, uvlock,
                                new Variant(resourceLocation, TRANSFORMATION_2D, uvlock, 1)));
                    }
                } catch (Exception e) {
                    Constants.LOG.warn("Failed to generate vertical slab item model for: {}", resourceLocation.toString());
                    Constants.LOG.error(e.getMessage());
                }

            }
        });

        Constants.LOG.debug("Loaded vertical slab models!");
    }



}
