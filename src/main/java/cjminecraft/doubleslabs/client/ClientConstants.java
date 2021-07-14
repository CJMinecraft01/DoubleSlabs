package cjminecraft.doubleslabs.client;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.client.proxy.ClientProxy;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.vecmath.Quat4f;
import java.util.Map;
import java.util.Objects;

public class ClientConstants {

    private static final Map<BlockState, Map<Direction, IBakedModel>> VERTICAL_SLAB_MODELS = Maps.newIdentityHashMap();
    private static final Map<Item, IBakedModel> VERTICAL_SLAB_ITEM_MODELS = Maps.newIdentityHashMap();
    private static final TRSRTransformation TRANSFORMATION_2D = new TRSRTransformation(null, quaternionConvert(new Quaternion(new Vector3f(0, 0, -1), 90, true)), null, null);
    public static final int TINT_OFFSET = 1000;

    private static Quat4f quaternionConvert(Quaternion quat) {
        return new Quat4f(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    public static boolean isTransparent(BlockState state) {
        return !state.getMaterial().isOpaque() || !state.isSolid();
    }

    public static IBakedModel getFallbackModel() {
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
    }

    public static IBakedModel getVerticalModel(BlockState state, Direction direction) {
        Map<Direction, IBakedModel> map = VERTICAL_SLAB_MODELS.get(state);
        if (map != null)
            return map.getOrDefault(direction, getFallbackModel());
        return getFallbackModel();
    }

    public static IBakedModel getVerticalModel(Item item) {
        return VERTICAL_SLAB_ITEM_MODELS.getOrDefault(item, getFallbackModel());
    }

    public static IBakedModel bake(ModelLoader modelLoader, IUnbakedModel baseModel, ResourceLocation location, boolean uvlock, ISprite transform) {
        if (baseModel instanceof VariantList) {
            VariantList model = (VariantList) baseModel;
            if (model.getVariantList().isEmpty()) {
                return null;
            } else {
                WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();

                for(Variant variant : model.getVariantList()) {
//                    IBakedModel ibakedmodel = modelLoader.getBakedModel(variant.getModelLocation(), new ModelTransformComposition(variant, transform, uvlock), modelLoader.getSpriteMap()::getSprite);
                    IBakedModel ibakedmodel = modelLoader.getBakedModel(variant.getModelLocation(), transform, ModelLoader.defaultTextureGetter(), DefaultVertexFormats.ITEM);
                    builder.add(ibakedmodel, variant.getWeight());
                }

                return builder.build();
            }
        } else {
            return baseModel.bake(modelLoader, ModelLoader.defaultTextureGetter(), transform, DefaultVertexFormats.ITEM);
        }
    }

    public static void bakeVerticalSlabModels(ModelLoader modelLoader) {
        VERTICAL_SLAB_MODELS.clear();
        VERTICAL_SLAB_ITEM_MODELS.clear();

        ForgeRegistries.BLOCKS.forEach(block -> {
            if (SlabSupport.isHorizontalSlab(block)) {
                boolean raisedCampfire = block instanceof RaisedCampfireBlock;
                boolean uvlock = DSConfig.CLIENT.uvlock(block);
                block.getStateContainer().getValidStates().forEach(state -> {
                    ModelResourceLocation resourceLocation = BlockModelShapes.getModelLocation(state);
                    try {
                        Map<Direction, IBakedModel> map = Maps.newEnumMap(Direction.class);
                        IUnbakedModel model = modelLoader.getUnbakedModel(resourceLocation);
                        if (raisedCampfire) {
                            map.put(Direction.NORTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new BasicState(TRSRTransformation.from(ModelRotation.X90_Y180).compose(ClientProxy.RAISED_CAMPFIRE_TRANSFORM), uvlock)));
                            map.put(Direction.EAST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new BasicState(TRSRTransformation.from(ModelRotation.X90_Y270).compose(ClientProxy.RAISED_CAMPFIRE_TRANSFORM), uvlock)));
                            map.put(Direction.SOUTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new BasicState(TRSRTransformation.from(ModelRotation.X90_Y0).compose(ClientProxy.RAISED_CAMPFIRE_TRANSFORM), uvlock)));
                            map.put(Direction.WEST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new BasicState(TRSRTransformation.from(ModelRotation.X90_Y90).compose(ClientProxy.RAISED_CAMPFIRE_TRANSFORM), uvlock)));
                        } else {
                            map.put(Direction.NORTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new BasicState(ModelRotation.X90_Y180.getRotation(), uvlock)));
                            map.put(Direction.EAST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new BasicState(ModelRotation.X90_Y270.getRotation(), uvlock)));
                            map.put(Direction.SOUTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new BasicState(ModelRotation.X90_Y0.getRotation(), uvlock)));
                            map.put(Direction.WEST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new BasicState(ModelRotation.X90_Y90.getRotation(), uvlock)));
                        }
                        VERTICAL_SLAB_MODELS.put(state, map);
                    } catch (Exception e) {
                        DoubleSlabs.LOGGER.warn("Failed to generate vertical slab model for: {}", resourceLocation.toString());
                        DoubleSlabs.LOGGER.catching(e);
                    }
                });
            }
        });


        ForgeRegistries.ITEMS.forEach(item -> {
            if (SlabSupport.isHorizontalSlab(item)) {
                boolean uvlock = item instanceof BlockItem ? DSConfig.CLIENT.uvlock(((BlockItem) item).getBlock()) : DSConfig.CLIENT.uvlock(item);
                ModelResourceLocation resourceLocation = new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory");
                try {
                    IUnbakedModel model = modelLoader.getUnbakedModel(resourceLocation);
                    boolean is3d = (model instanceof BlockModel) && ((BlockModel) model).isGui3d();
                    if (is3d) {
                        VERTICAL_SLAB_ITEM_MODELS.put(item, bake(modelLoader, model, resourceLocation, uvlock,
                                new BasicState(ModelRotation.X90_Y0.getRotation(), uvlock)));
                    } else {
                        // Rotate 90 for 2d model
                        VERTICAL_SLAB_ITEM_MODELS.put(item, modelLoader.getBakedModel(resourceLocation,
                                new BasicState(TRANSFORMATION_2D, uvlock),
                                ModelLoader.defaultTextureGetter(), DefaultVertexFormats.ITEM));
                    }
                } catch (Exception e) {
                    DoubleSlabs.LOGGER.warn("Failed to generate vertical slab item model for: {}", resourceLocation.toString());
                    DoubleSlabs.LOGGER.catching(e);
                }

            }
        });

        DoubleSlabs.LOGGER.debug("Loaded vertical slab models!");
    }

}
