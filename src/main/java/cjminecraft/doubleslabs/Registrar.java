package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Registrar {

    public static BlockDoubleSlab DOUBLE_SLAB = new BlockDoubleSlab();

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(DOUBLE_SLAB);

        GameRegistry.registerTileEntity(TileEntityDoubleSlab.class, new ResourceLocation(DoubleSlabs.MODID, "double_slab"));
    }

	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        event.getModelRegistry().putObject(DoubleSlabBakedModel.variantTag, new DoubleSlabBakedModel());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerItemColours(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(DOUBLE_SLAB.getBlockColor(), DOUBLE_SLAB);
    }

}
