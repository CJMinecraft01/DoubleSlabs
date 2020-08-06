package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {

    public static BlockDoubleSlab DOUBLE_SLAB = new BlockDoubleSlab();
    public static BlockVerticalSlab VERTICAL_SLAB = new BlockVerticalSlab();
    public static TileEntityType<TileEntityDoubleSlab> TILE_DOUBLE_SLAB;
    public static TileEntityType<TileEntityVerticalSlab> TILE_VERTICAL_SLAB;

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(DOUBLE_SLAB, VERTICAL_SLAB);
//            RenderTypeLookup.setRenderLayer(DOUBLE_SLAB, RenderType.getCutout());
    }

    @SubscribeEvent
    public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
        TILE_DOUBLE_SLAB = TileEntityType.Builder.create(TileEntityDoubleSlab::new, DOUBLE_SLAB).build(null);
        TILE_DOUBLE_SLAB.setRegistryName(DoubleSlabs.MODID, "double_slabs");
        TILE_VERTICAL_SLAB = TileEntityType.Builder.create(TileEntityVerticalSlab::new, VERTICAL_SLAB).build(null);
        TILE_VERTICAL_SLAB.setRegistryName(DoubleSlabs.MODID, "vertical_slab");

        event.getRegistry().registerAll(TILE_DOUBLE_SLAB, TILE_VERTICAL_SLAB);
    }

}
