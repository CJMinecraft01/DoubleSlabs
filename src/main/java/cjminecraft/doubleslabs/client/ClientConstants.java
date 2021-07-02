package cjminecraft.doubleslabs.client;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.util.Vector3f;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientConstants {

    public static final int TINT_OFFSET = 1000;
    private static final TRSRTransformation TRANSFORMATION_2D = new TRSRTransformation(null, Vector3f.ZN.rotationDegrees(90).toQuat(), null, null);
    private static final Map<IBlockState, Map<EnumFacing, IBakedModel>> VERTICAL_SLAB_MODELS = Maps.newIdentityHashMap();
    private static final Map<ModelResourceLocation, IBakedModel> VERTICAL_SLAB_ITEM_MODELS = Maps.newHashMap();
    private static final Class<?> WEIGHTED_RANDOM_MODEL;
    private static final Constructor<?> WEIGHTED_RANDOM_MODEL_CONSTRUCTOR;
    private static final Field WEIGHTED_RANDOM_MODEL_VARIANTS;

    static {
        Class<?> weightedRandomModel;
        Constructor<?> weightedRandomModelConstructor;
        Field weightedRandomModelVariants;

        try {
            weightedRandomModel = Class.forName("net.minecraftforge.client.model.ModelLoader$WeightedRandomModel");
            weightedRandomModelConstructor = weightedRandomModel.getConstructor(ResourceLocation.class, VariantList.class);
            weightedRandomModelConstructor.setAccessible(true);
            weightedRandomModelVariants = weightedRandomModel.getDeclaredField("variants");
            weightedRandomModelVariants.setAccessible(true);
        } catch (Exception ignore) {
            weightedRandomModel = null;
            weightedRandomModelConstructor = null;
            weightedRandomModelVariants = null;
        }

        WEIGHTED_RANDOM_MODEL = weightedRandomModel;
        WEIGHTED_RANDOM_MODEL_CONSTRUCTOR = weightedRandomModelConstructor;
        WEIGHTED_RANDOM_MODEL_VARIANTS = weightedRandomModelVariants;
    }

    public static boolean isTransparent(IBlockState state) {
        return !state.getMaterial().isOpaque() || state.isTranslucent();
    }

    public static IBakedModel getFallbackModel() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
    }

    public static IBakedModel getVerticalModel(IBlockState state, EnumFacing direction) {
        Map<EnumFacing, IBakedModel> map = VERTICAL_SLAB_MODELS.get(state);
        if (map != null)
            return map.getOrDefault(direction, getFallbackModel());
        return getFallbackModel();
    }

    public static IBakedModel getVerticalModel(ItemStack stack) {
        ModelResourceLocation location = ((ItemModelMesherForge) Minecraft.getMinecraft().getRenderItem().getItemModelMesher()).getLocation(stack);
        return VERTICAL_SLAB_ITEM_MODELS.getOrDefault(location, getFallbackModel());
    }

    public static IBakedModel bake(ModelLoader modelLoader, VariantList variants, boolean uvlock, ModelRotation rotation) {
        if (variants.getVariantList().isEmpty()) {
            return null;
        } else {
            WeightedBakedModel.Builder weightedbakedmodel$builder = new WeightedBakedModel.Builder();
            int i = 0;

            for (Variant variant : variants.getVariantList()) {
                ModelBlock modelblock = modelLoader.models.get(variant.getModelLocation());

                if (modelblock != null && modelblock.isResolved()) {
                    if (!modelblock.getElements().isEmpty()) {
                        IBakedModel ibakedmodel = modelLoader.bakeModel(modelblock, rotation, variant.isUvLock() || uvlock);

                        if (ibakedmodel != null) {
                            ++i;
                            weightedbakedmodel$builder.add(ibakedmodel, variant.getWeight());
                        }
                    }
                }
            }

            IBakedModel ibakedmodel1 = null;

            if (i == 1) {
                ibakedmodel1 = weightedbakedmodel$builder.first();
            } else if (i != 0) {
                ibakedmodel1 = weightedbakedmodel$builder.build();
            }

            return ibakedmodel1;
        }
    }

    private static IModel newWeightedRandomModel(VariantList variants) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (IModel) WEIGHTED_RANDOM_MODEL_CONSTRUCTOR.newInstance(new ResourceLocation(""), variants);
    }

    private static List<Variant> getVariantListFromWeightedRandomModel(IModel model) throws IllegalAccessException {
        Object obj = WEIGHTED_RANDOM_MODEL.cast(model);
        return (List<Variant>) WEIGHTED_RANDOM_MODEL_VARIANTS.get(obj);
    }

    public static IBakedModel bake(ModelLoader modelLoader, IModel model, ModelRotation rotation, boolean uvlock) {
        final Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> modelLoader.textureMap.getAtlasSprite(location.toString());

        Optional<ModelBlock> vanillaModel = model.asVanillaModel();
        if (vanillaModel.isPresent()) {
            ModelBlock block = vanillaModel.get();
            TextureAtlasSprite textureatlassprite = modelLoader.textureMap.getAtlasSprite(new ResourceLocation(block.resolveTextureName("particle")).toString());
            SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(block, block.createOverrides())).setTexture(textureatlassprite);

            if (block.getElements().isEmpty()) {
                return getFallbackModel();
            } else {
                for (BlockPart blockpart : block.getElements()) {
                    for (EnumFacing enumfacing : blockpart.mapFaces.keySet()) {
                        BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
                        TextureAtlasSprite textureatlassprite1 = modelLoader.textureMap.getAtlasSprite(new ResourceLocation(block.resolveTextureName(blockpartface.texture)).toString());

                        if (blockpartface.cullFace == null || !net.minecraftforge.common.model.TRSRTransformation.isInteger(rotation.getMatrix())) {
                            simplebakedmodel$builder.addGeneralQuad(modelLoader.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, rotation, uvlock));
                        } else {
                            simplebakedmodel$builder.addFaceQuad(rotation.rotate(blockpartface.cullFace), modelLoader.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, rotation, uvlock));
                        }
                    }
                }

                return simplebakedmodel$builder.makeBakedModel();
            }
        } else if (WEIGHTED_RANDOM_MODEL != null && WEIGHTED_RANDOM_MODEL_VARIANTS != null && WEIGHTED_RANDOM_MODEL_CONSTRUCTOR != null && WEIGHTED_RANDOM_MODEL.isAssignableFrom(model.getClass())) {
            try {
                IModel newModel = newWeightedRandomModel(new VariantList(getVariantListFromWeightedRandomModel(model).stream().map(v -> new Variant(v.getModelLocation(), v.getRotation(), v.isUvLock() || uvlock, v.getWeight())).collect(Collectors.toList())));
                return newModel.bake(rotation, DefaultVertexFormats.ITEM, textureGetter);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException ignored) {

            }
        }
        return model.bake(rotation, DefaultVertexFormats.ITEM, textureGetter);
    }

    public static void bakeVerticalSlabModels(ModelLoader modelLoader) {
        VERTICAL_SLAB_MODELS.clear();
        VERTICAL_SLAB_ITEM_MODELS.clear();

        BlockStateMapper mapper = modelLoader.blockModelShapes.getBlockStateMapper();

        ForgeRegistries.BLOCKS.forEach(block -> {
            if (SlabSupport.isHorizontalSlab(block)) {
                Map<IBlockState, ModelResourceLocation> variants = mapper.getVariants(block);
                block.getBlockState().getValidStates().forEach(state -> {
                    boolean uvlock = DSConfig.CLIENT.uvlock(state);
                    ModelResourceLocation resourceLocation = variants.get(state);
                    if (resourceLocation != null) {
                        try {
                            IModel model = ModelLoaderRegistry.getModel(resourceLocation).uvlock(uvlock);
                            Map<EnumFacing, IBakedModel> map = Maps.newEnumMap(EnumFacing.class);
                            map.put(EnumFacing.NORTH, bake(modelLoader, model, ModelRotation.X90_Y180, uvlock));
                            map.put(EnumFacing.EAST, bake(modelLoader, model, ModelRotation.X90_Y270, uvlock));
                            map.put(EnumFacing.SOUTH, bake(modelLoader, model, ModelRotation.X90_Y0, uvlock));
                            map.put(EnumFacing.WEST, bake(modelLoader, model, ModelRotation.X90_Y90, uvlock));
                            VERTICAL_SLAB_MODELS.put(state, map);
                        } catch (Exception e) {
                            DoubleSlabs.LOGGER.warn("Failed to generate vertical slab model for: {}", resourceLocation.toString());
                            DoubleSlabs.LOGGER.catching(e);
                        }
                    }
                });
//                for (final ResourceLocation resourcelocation : mapper.getBlockstateLocations(block)) {
//                    DoubleSlabs.LOGGER.info(resourcelocation.toString());
//                    Map<IBlockState, ModelResourceLocation> variants = mapper.getVariants(block);
//
//                    variants.forEach((state, modelResourceLocation) -> {
//                        DoubleSlabs.LOGGER.info(modelResourceLocation.toString());
//                        if (resourcelocation.equals(modelResourceLocation)) {
//                            try {
//                                VariantList list = modelLoader.variants.get(modelResourceLocation);
//                                DoubleSlabs.LOGGER.info(list);
//                                Map<EnumFacing, IBakedModel> map = Maps.newEnumMap(EnumFacing.class);
//                                map.put(EnumFacing.NORTH, bake(modelLoader, list, uvlock, ModelRotation.X90_Y180));
//                                map.put(EnumFacing.EAST, bake(modelLoader, list, uvlock, ModelRotation.X90_Y270));
//                                map.put(EnumFacing.SOUTH, bake(modelLoader, list, uvlock, ModelRotation.X90_Y0));
//                                map.put(EnumFacing.WEST, bake(modelLoader, list, uvlock, ModelRotation.X90_Y90));
//                                VERTICAL_SLAB_MODELS.put(state, map);
//                            } catch (Exception e) {
//                                DoubleSlabs.LOGGER.warn("Failed to generate vertical slab model for: {}", modelResourceLocation.toString());
//                                DoubleSlabs.LOGGER.catching(e);
//                            }
//                        }
//                    });
//                }
            }
        });


        ForgeRegistries.ITEMS.forEach(item -> {
            if (SlabSupport.isHorizontalSlab(item)) {
                for (String s : modelLoader.getVariantNames(item)) {
                    ModelResourceLocation modelResourceLocation = ModelLoader.getInventoryVariant(s);
                    boolean uvlock = !DSConfig.CLIENT.uvlockModelBlacklist.contains(modelResourceLocation.getNamespace() + ":" + modelResourceLocation.getPath());
                    try {
                        IModel model = ModelLoaderRegistry.getModel(modelResourceLocation).uvlock(uvlock);
                        VERTICAL_SLAB_ITEM_MODELS.put(modelResourceLocation, bake(modelLoader, model, ModelRotation.X90_Y0, uvlock));
                    } catch (Exception e) {
                        DoubleSlabs.LOGGER.warn("Failed to generate vertical slab item model for: {}", modelResourceLocation.toString());
                        DoubleSlabs.LOGGER.catching(e);
                    }
                }
            }
        });

        DoubleSlabs.LOGGER.debug("Loaded vertical slab models!");
    }

}
