package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.client.model.VerticalSlabBakedModel;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Registrar {

    public static BlockDoubleSlab DOUBLE_SLAB = new BlockDoubleSlab();
    public static BlockVerticalSlab VERTICAL_SLAB = new BlockVerticalSlab();

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(DOUBLE_SLAB, VERTICAL_SLAB);

        GameRegistry.registerTileEntity(TileEntityDoubleSlab.class, new ResourceLocation(DoubleSlabs.MODID, "double_slab"));
        GameRegistry.registerTileEntity(TileEntityVerticalSlab.class, new ResourceLocation(DoubleSlabs.MODID, "vertical_slab"));
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

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        event.getModelRegistry().putObject(DoubleSlabBakedModel.variantTag, new DoubleSlabBakedModel());
        VerticalSlabBakedModel verticalSlabBakedModel = new VerticalSlabBakedModel();
        for (IBlockState state : VERTICAL_SLAB.getBlockState().getValidStates()) {
            ModelResourceLocation variantResourceLocation = new ModelResourceLocation(Block.REGISTRY.getNameForObject(VERTICAL_SLAB), getPropertyString(state.getProperties()));
            IBakedModel existingModel = event.getModelRegistry().getObject(variantResourceLocation);
            if (existingModel == null) {
                DoubleSlabs.LOGGER.warn("Did not find the expected vanilla baked model(s) for the vertical slab in registry");
            } else if (existingModel instanceof VerticalSlabBakedModel) {
                DoubleSlabs.LOGGER.warn("Tried to replace VerticalSlabBakedModel twice");
            } else {
                verticalSlabBakedModel.addModel(existingModel, state);
                event.getModelRegistry().putObject(variantResourceLocation, verticalSlabBakedModel);
            }
        }
//        event.getModelRegistry().putObject(VerticalSlabBakedModel.variantTag, model);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerBlockColours(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(DOUBLE_SLAB.getBlockColor(), DOUBLE_SLAB);
        event.getBlockColors().registerBlockColorHandler(VERTICAL_SLAB.getBlockColor(), VERTICAL_SLAB);
    }

}
