package cjminecraft.doubleslabs.client;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.libraryex.LibraryExSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.util.Vector3f;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockSlab;
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
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
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
    private static final Class<?> FORGE_VARIANT;
    private static final Constructor<?> FORGE_VARIANT_CONSTRUCTOR;
    private static final Field FORGE_VARIANT_BLOCKSTATE_LOCATION;
    private static final Field FORGE_VARIANT_TEXTURES;
    private static final Field FORGE_VARIANT_PARTS;
    private static final Field FORGE_VARIANT_CUSTOMDATA;
    private static final Field FORGE_VARIANT_STATE;
    private static final Field FORGE_VARIANT_SMOOTH;
    private static final Field FORGE_VARIANT_GUI3D;

    static {
        Class<?> weightedRandomModel;
        Constructor<?> weightedRandomModelConstructor;
        Field weightedRandomModelVariants;
        Class<?> forgeVariant;
        Constructor<?> forgeVariantConstructor;
        Field forgeVariantBlockstateLocation;
        Field forgeVariantTextures;
        Field forgeVariantParts;
        Field forgeVariantCustomData;
        Field forgeVariantState;
        Field forgeVariantSmooth;
        Field forgeVariantGui3d;

        try {
            weightedRandomModel = Class.forName("net.minecraftforge.client.model.ModelLoader$WeightedRandomModel");
            weightedRandomModelConstructor = weightedRandomModel.getConstructor(ResourceLocation.class, VariantList.class);
            weightedRandomModelConstructor.setAccessible(true);
            weightedRandomModelVariants = weightedRandomModel.getDeclaredField("variants");
            weightedRandomModelVariants.setAccessible(true);
            forgeVariant = Class.forName("net.minecraftforge.client.model.BlockStateLoader$ForgeVariant");
            forgeVariantConstructor = forgeVariant.getConstructor(ResourceLocation.class, ResourceLocation.class, IModelState.class, boolean.class, Optional.class, Optional.class, int.class, ImmutableMap.class, ImmutableMap.class, ImmutableMap.class);
            forgeVariantConstructor.setAccessible(true);
            forgeVariantBlockstateLocation = forgeVariant.getDeclaredField("blockstateLocation");
            forgeVariantBlockstateLocation.setAccessible(true);
            forgeVariantTextures = forgeVariant.getDeclaredField("textures");
            forgeVariantTextures.setAccessible(true);
            forgeVariantParts = forgeVariant.getDeclaredField("parts");
            forgeVariantParts.setAccessible(true);
            forgeVariantCustomData = forgeVariant.getDeclaredField("customData");
            forgeVariantCustomData.setAccessible(true);
            forgeVariantSmooth = forgeVariant.getDeclaredField("smooth");
            forgeVariantSmooth.setAccessible(true);
            forgeVariantGui3d = forgeVariant.getDeclaredField("gui3d");
            forgeVariantGui3d.setAccessible(true);
            forgeVariantState = forgeVariant.getDeclaredField("state");
            forgeVariantState.setAccessible(true);

        } catch (Exception ignore) {
            weightedRandomModel = null;
            weightedRandomModelConstructor = null;
            weightedRandomModelVariants = null;
            forgeVariant = null;
            forgeVariantConstructor = null;
            forgeVariantBlockstateLocation = null;
            forgeVariantTextures = null;
            forgeVariantParts = null;
            forgeVariantCustomData = null;
            forgeVariantState = null;
            forgeVariantSmooth = null;
            forgeVariantGui3d = null;
        }

        WEIGHTED_RANDOM_MODEL = weightedRandomModel;
        WEIGHTED_RANDOM_MODEL_CONSTRUCTOR = weightedRandomModelConstructor;
        WEIGHTED_RANDOM_MODEL_VARIANTS = weightedRandomModelVariants;
        FORGE_VARIANT = forgeVariant;
        FORGE_VARIANT_CONSTRUCTOR = forgeVariantConstructor;
        FORGE_VARIANT_BLOCKSTATE_LOCATION = forgeVariantBlockstateLocation;
        FORGE_VARIANT_TEXTURES = forgeVariantTextures;
        FORGE_VARIANT_PARTS = forgeVariantParts;
        FORGE_VARIANT_CUSTOMDATA = forgeVariantCustomData;
        FORGE_VARIANT_STATE = forgeVariantState;
        FORGE_VARIANT_SMOOTH = forgeVariantSmooth;
        FORGE_VARIANT_GUI3D = forgeVariantGui3d;
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

    private static IModel newWeightedRandomModel(VariantList variants) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (IModel) WEIGHTED_RANDOM_MODEL_CONSTRUCTOR.newInstance(new ResourceLocation(""), variants);
    }

    private static List<Variant> getVariantListFromWeightedRandomModel(IModel model) throws IllegalAccessException {
        Object obj = WEIGHTED_RANDOM_MODEL.cast(model);
        return (List<Variant>) WEIGHTED_RANDOM_MODEL_VARIANTS.get(obj);
    }

    private static Variant convertVariant(Variant variant, boolean uvlock) {
        if (FORGE_VARIANT != null && FORGE_VARIANT.isAssignableFrom(variant.getClass())) {
            try {
                Object obj = FORGE_VARIANT.cast(variant);
                return (Variant) FORGE_VARIANT_CONSTRUCTOR.newInstance(
                        (ResourceLocation) FORGE_VARIANT_BLOCKSTATE_LOCATION.get(obj),
                        variant.getModelLocation(),
                        (IModelState) FORGE_VARIANT_STATE.get(obj),
                        variant.isUvLock() || uvlock,
                        (Optional<Boolean>) FORGE_VARIANT_SMOOTH.get(obj),
                        (Optional<Boolean>) FORGE_VARIANT_GUI3D.get(obj),
                        variant.getWeight(),
                        (ImmutableMap<String, String>) FORGE_VARIANT_TEXTURES.get(obj),
                        (ImmutableMap<String, BlockStateLoader.SubModel>) FORGE_VARIANT_PARTS.get(obj),
                        (ImmutableMap<String, String>) FORGE_VARIANT_CUSTOMDATA.get(obj)
                        );
            } catch (Exception ignored) {

            }
        }
        return new Variant(variant.getModelLocation(), variant.getRotation(), variant.isUvLock() || uvlock, variant.getWeight());
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
                IModel newModel = newWeightedRandomModel(new VariantList(getVariantListFromWeightedRandomModel(model).stream().map(v -> convertVariant(v, uvlock)).collect(Collectors.toList())));
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
            IHorizontalSlabSupport slabSupport = SlabSupport.getHorizontalSlabSupport(block);
            if (slabSupport != null) {
                Map<IBlockState, ModelResourceLocation> variants = mapper.getVariants(block);
                block.getBlockState().getValidStates().forEach(state -> {
                    boolean uvlock = DSConfig.CLIENT.uvlock(state);
                    ModelResourceLocation resourceLocation = variants.get(state);
                    if (resourceLocation != null) {
                        // Quark and netherex fix
                        boolean fix = resourceLocation.getNamespace().equals("quark") && state.getValue(BlockSlab.HALF).equals(BlockSlab.EnumBlockHalf.TOP);
                        if (slabSupport instanceof LibraryExSlabSupport)
                            fix = slabSupport.getHalf(null, null, state) == BlockSlab.EnumBlockHalf.TOP;
                        try {
                            IModel model = ModelLoaderRegistry.getModel(resourceLocation).uvlock(uvlock);
                            Map<EnumFacing, IBakedModel> map = Maps.newEnumMap(EnumFacing.class);
                            map.put(EnumFacing.NORTH, bake(modelLoader, model, fix ? ModelRotation.X90_Y0 : ModelRotation.X90_Y180, uvlock));
                            map.put(EnumFacing.EAST, bake(modelLoader, model, fix ? ModelRotation.X90_Y90 : ModelRotation.X90_Y270, uvlock));
                            map.put(EnumFacing.SOUTH, bake(modelLoader, model, fix ? ModelRotation.X90_Y180 : ModelRotation.X90_Y0, uvlock));
                            map.put(EnumFacing.WEST, bake(modelLoader, model, fix ? ModelRotation.X90_Y270 : ModelRotation.X90_Y90, uvlock));
                            VERTICAL_SLAB_MODELS.put(state, map);
                        } catch (Exception e) {
                            DoubleSlabs.LOGGER.warn("Failed to generate vertical slab model for: {}", resourceLocation.toString());
                            DoubleSlabs.LOGGER.catching(e);
                        }
                    }
                });
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
