package cjminecraft.doubleslabs.old.test;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

//@Mod.EventBusSubscriber(modid = DoubleSlabsTest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {

    public static final Block GLASS_SLAB = new GlassSlab().setRegistryName(new ResourceLocation(DoubleSlabsTest.MODID, "glass_slab"));

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(GLASS_SLAB);
        if (FMLEnvironment.dist == Dist.CLIENT)
            RenderTypeLookup.setRenderLayer(GLASS_SLAB, RenderType.getCutout());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(GLASS_SLAB, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(GLASS_SLAB.getRegistryName()));
    }
}
