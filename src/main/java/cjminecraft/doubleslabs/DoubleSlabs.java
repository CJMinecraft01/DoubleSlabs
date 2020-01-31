package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.proxy.ClientProxy;
import cjminecraft.doubleslabs.proxy.IProxy;
import cjminecraft.doubleslabs.proxy.ServerProxy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


@Mod(DoubleSlabs.MODID)
public class DoubleSlabs
{
    public static final String NAME = "DoubleSlabs";
    public static final String MODID = "doubleslabs";
//    public static final String VERSION = "${version}";
//    public static final String ACCEPTED_MC_VERSIONS = "[1.12,1.12.2]";
//    public static final String UPDATE_URL = "https://raw.githubusercontent.com/CJMinecraft01/DoubleSlabs/master/update.json";
    public static final Logger LOGGER = LogManager.getFormatterLogger(NAME);

    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public DoubleSlabs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
//        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
//        LOGGER.info(Config.SLAB_BLACKLIST.get());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        proxy.setup(bus);
    }

    public void setup(FMLCommonSetupEvent event) {

    }

//    @Mod.EventHandler
//    public void init(FMLInitializationEvent event) {
//        ConfigManager.sync(MODID, Config.Type.INSTANCE);
//        proxy.init();
//        DoubleSlabsConfig.slabBlacklist = Arrays.asList(DoubleSlabsConfig.slabBlacklistArray);
//    }
//
//    @Mod.EventHandler
//    public void postInit(FMLPostInitializationEvent event) {
//        proxy.postInit();
//    }
}
