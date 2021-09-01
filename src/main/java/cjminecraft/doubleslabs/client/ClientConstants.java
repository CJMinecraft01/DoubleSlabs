package cjminecraft.doubleslabs.client;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.client.proxy.ClientProxy;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.RaisedCampfireBlock;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

public class ClientConstants {

    private static final Map<BlockState, Map<Direction, IBakedModel>> VERTICAL_SLAB_MODELS = Maps.newIdentityHashMap();
    private static final Map<Item, IBakedModel> VERTICAL_SLAB_ITEM_MODELS = Maps.newIdentityHashMap();
    private static final TransformationMatrix TRANSFORMATION_2D = new TransformationMatrix(null, Vector3f.ZN.rotationDegrees(90), null, null);
    public static final int TINT_OFFSET = 1000;

    public static String getModName(IForgeRegistryEntry<?> entry) {
        ResourceLocation registryName = entry.getRegistryName();
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return ModList.get().getModContainerById(modId)
                .map(mod -> mod.getModInfo().getDisplayName())
                .orElseGet(() -> StringUtils.capitalize(modId));
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

    public static IBakedModel bake(ModelLoader modelLoader, IUnbakedModel baseModel, ResourceLocation location, boolean uvlock, IModelTransform transform) {
        if (baseModel instanceof VariantList) {
            VariantList model = (VariantList) baseModel;
            if (model.getVariantList().isEmpty()) {
                return null;
            } else {
                WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();

                for(Variant variant : model.getVariantList()) {
                    IBakedModel ibakedmodel = modelLoader.getBakedModel(variant.getModelLocation(), transform, modelLoader.getSpriteMap()::getSprite);
                    builder.add(ibakedmodel, variant.getWeight());
                }

                return builder.build();
            }
        } else {
            return baseModel.bakeModel(modelLoader, modelLoader.getSpriteMap()::getSprite, transform, location);
        }
    }

    public static void bakeVerticalSlabModels(ModelLoader modelLoader) {
        VERTICAL_SLAB_MODELS.clear();
        VERTICAL_SLAB_ITEM_MODELS.clear();

        ForgeRegistries.BLOCKS.forEach(block -> {
            ISlabSupport support = SlabSupport.getHorizontalSlabSupport(block);
            if (support != null) {
                boolean raisedCampfire = block instanceof RaisedCampfireBlock;
                boolean uvlock = support.uvlock(block) && DSConfig.CLIENT.uvlock(block);
                block.getStateContainer().getValidStates().forEach(state -> {
                    ModelResourceLocation resourceLocation = BlockModelShapes.getModelLocation(state);
                    try {
                        Map<Direction, IBakedModel> map = Maps.newEnumMap(Direction.class);
                        IUnbakedModel model = modelLoader.getUnbakedModel(resourceLocation);
                        if (raisedCampfire) {
                            map.put(Direction.NORTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, ModelRotation.X90_Y180.getRotation().compose(ClientProxy.RAISED_CAMPFIRE_TRANSFORM), uvlock, 1)));
                            map.put(Direction.EAST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, ModelRotation.X90_Y270.getRotation().compose(ClientProxy.RAISED_CAMPFIRE_TRANSFORM), uvlock, 1)));
                            map.put(Direction.SOUTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, ModelRotation.X90_Y0.getRotation().compose(ClientProxy.RAISED_CAMPFIRE_TRANSFORM), uvlock, 1)));
                            map.put(Direction.WEST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, ModelRotation.X90_Y90.getRotation().compose(ClientProxy.RAISED_CAMPFIRE_TRANSFORM), uvlock, 1)));
                        } else {
                            map.put(Direction.NORTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, ModelRotation.X90_Y180.getRotation(), uvlock, 1)));
                            map.put(Direction.EAST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, ModelRotation.X90_Y270.getRotation(), uvlock, 1)));
                            map.put(Direction.SOUTH, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, ModelRotation.X90_Y0.getRotation(), uvlock, 1)));
                            map.put(Direction.WEST, bake(modelLoader, model, resourceLocation, uvlock,
                                    new Variant(resourceLocation, ModelRotation.X90_Y90.getRotation(), uvlock, 1)));
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
                    boolean is3d = (model instanceof BlockModel) && ((BlockModel) model).func_230176_c_().func_230178_a_();
                    if (is3d) {
                        VERTICAL_SLAB_ITEM_MODELS.put(item, bake(modelLoader, model, resourceLocation, uvlock,
                                new Variant(resourceLocation, ModelRotation.X90_Y0.getRotation(), uvlock, 1)));
                    } else {
                        // Rotate 90 for 2d model
                        VERTICAL_SLAB_ITEM_MODELS.put(item, bake(modelLoader, model, resourceLocation, uvlock,
                                new Variant(resourceLocation, TRANSFORMATION_2D, uvlock, 1)));
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
