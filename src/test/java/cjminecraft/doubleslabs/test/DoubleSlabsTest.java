package cjminecraft.doubleslabs.test;

import cjminecraft.doubleslabs.Config;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DoubleSlabsTest.MODID)
public class DoubleSlabsTest {
    public static final String MODID = "doubleslabstest";

//    public DoubleSlabsTest() {
//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
//
//        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
////        bus.addListener(this::setup);
////        proxy.setup(bus);
//    }
}
