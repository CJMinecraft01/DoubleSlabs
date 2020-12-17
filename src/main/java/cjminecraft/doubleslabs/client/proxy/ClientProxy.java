package cjminecraft.doubleslabs.client.proxy;

import cjminecraft.doubleslabs.client.model.*;
import cjminecraft.doubleslabs.client.render.SlabTileEntityRenderer;
import cjminecraft.doubleslabs.client.util.ClientUtils;
import cjminecraft.doubleslabs.client.util.vertex.VerticalSlabTransformer;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.init.DSKeyBindings;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;
import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = DoubleSlabs.MODID)
public class ClientProxy implements IProxy {

    @Override
    public void preInit() {
        DSKeyBindings.register();
        ClientRegistry.bindTileEntitySpecialRenderer(SlabTileEntity.class, new SlabTileEntityRenderer());

        ModelLoader.setCustomModelResourceLocation(DSItems.VERTICAL_SLAB, 0, new ModelResourceLocation(DSItems.VERTICAL_SLAB.getRegistryName(), "inventory"));
    }

    @Override
    public void postInit() {
        ClientUtils.checkOptiFineInstalled();
    }

    private static String getPropertyString(Map<IProperty<?>, Comparable<?>> values) {
        StringBuilder stringbuilder = new StringBuilder();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : values.entrySet()) {
            if (stringbuilder.length() != 0) {
                stringbuilder.append(",");
            }

            IProperty<?> iproperty = entry.getKey();
            stringbuilder.append(iproperty.getName());
            stringbuilder.append("=");
            stringbuilder.append(getPropertyName(iproperty, entry.getValue()));
        }

        if (stringbuilder.length() == 0) {
            stringbuilder.append("normal");
        }

        return stringbuilder.toString();
    }

    private static <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }

    private static void replaceModel(IBakedModel model, Block block, BiConsumer<IBakedModel, IBlockState> perModel, IRegistry<ModelResourceLocation, IBakedModel> registry) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            if (state instanceof IExtendedBlockState)
                state = ((IExtendedBlockState) state).getClean();
            ModelResourceLocation variantResourceLocation = new ModelResourceLocation(Block.REGISTRY.getNameForObject(block), getPropertyString(state.getProperties()));
            IBakedModel existingModel = registry.getObject(variantResourceLocation);
            if (existingModel == null) {
                DoubleSlabs.LOGGER.warn("Did not find the expected vanilla baked model(s) for the block in registry");
            } else if (existingModel instanceof DynamicSlabBakedModel) {
                DoubleSlabs.LOGGER.warn("Tried to replace model twice");
            } else {
                perModel.accept(existingModel, state);
                registry.putObject(variantResourceLocation, model);
            }
        }
    }

    @SubscribeEvent
    public static void bakeModels(ModelBakeEvent event) {
        VerticalSlabTransformer.reload();

        replaceModel(new DoubleSlabBakedModel(), DSBlocks.DOUBLE_SLAB, (model, state) -> {}, event.getModelRegistry());
//        VerticalSlabBakedModel verticalSlabBakedModel = new VerticalSlabBakedModel();
        replaceModel(VerticalSlabBakedModel.INSTANCE, DSBlocks.VERTICAL_SLAB, VerticalSlabBakedModel.INSTANCE::addModel, event.getModelRegistry());

        ModelResourceLocation verticalSlabItemResourceLocation = new ModelResourceLocation(DSItems.VERTICAL_SLAB.getRegistryName(), "inventory");
        VerticalSlabItemBakedModel.INSTANCE = new VerticalSlabItemBakedModel(event.getModelRegistry().getObject(verticalSlabItemResourceLocation));
        event.getModelRegistry().putObject(verticalSlabItemResourceLocation, VerticalSlabItemBakedModel.INSTANCE);
    }

    @SubscribeEvent
    public static void registerBlockColours(final ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(DSBlocks.DOUBLE_SLAB.getBlockColor(), DSBlocks.DOUBLE_SLAB);
        event.getBlockColors().registerBlockColorHandler(DSBlocks.VERTICAL_SLAB.getBlockColor(), DSBlocks.VERTICAL_SLAB);
    }
}
