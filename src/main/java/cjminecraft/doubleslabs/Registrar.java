package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {

    public static BlockDoubleSlab DOUBLE_SLAB = new BlockDoubleSlab();
    public static TileEntityType TILE_DOUBLE_SLAB = TileEntityType.Builder.create(TileEntityDoubleSlab::new, DOUBLE_SLAB).build(null).setRegistryName(DoubleSlabs.MODID, "double_slabs");

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(DOUBLE_SLAB);
        if (FMLEnvironment.dist == Dist.CLIENT)
            RenderTypeLookup.setRenderLayer(DOUBLE_SLAB, RenderType.getCutout());
    }

    @SubscribeEvent
    public static void registerTE(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TILE_DOUBLE_SLAB);
    }

//    @OnlyIn(Dist.CLIENT)
//    @SubscribeEvent
//    public static void onModelBakeEvent(ModelBakeEvent event) {
//        event.getModelRegistry().put(DOUBLE_SLAB.getRegistryName(), new DoubleSlabBakedModel());
//    }

}
