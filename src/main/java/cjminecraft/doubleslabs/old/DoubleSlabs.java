package cjminecraft.doubleslabs.old;

import cjminecraft.doubleslabs.old.api.ContainerSupport;
import cjminecraft.doubleslabs.old.api.SlabSupport;
import cjminecraft.doubleslabs.old.client.render.TileEntityRendererDoubleSlab;
import cjminecraft.doubleslabs.old.client.render.TileEntityRendererVerticalSlab;
import cjminecraft.doubleslabs.old.network.PacketHandler;
import cjminecraft.doubleslabs.old.proxy.ClientProxy;
import cjminecraft.doubleslabs.old.proxy.IProxy;
import cjminecraft.doubleslabs.old.proxy.ServerProxy;
import cjminecraft.doubleslabs.old.tileentitiy.TileEntityVerticalSlab;
import cjminecraft.doubleslabs.old.tileentitiy.TileEntityDoubleSlab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Mod(DoubleSlabs.MODID)
public class DoubleSlabs {
    public static final String MODID = "doubleslabs";
    public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);

    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public DoubleSlabs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(this::clientSetup);
//        bus.addListener(this::onFingerprintViolation);
        proxy.setup(bus);
    }

    public void setup(FMLCommonSetupEvent event) {
        SlabSupport.init();
        ContainerSupport.init();
        PacketHandler.registerPackets();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        Utils.checkOptiFineInstalled();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVerticalSlab.class, new TileEntityRendererVerticalSlab());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDoubleSlab.class, new TileEntityRendererDoubleSlab());
    }

//    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
//        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
//    }

}
