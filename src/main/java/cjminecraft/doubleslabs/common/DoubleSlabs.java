package cjminecraft.doubleslabs.common;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.client.proxy.ClientProxy;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.config.ConfigEventsHandler;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.config.SyncConfigValue;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.init.DSTiles;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.common.util.AnnotationUtil;
import cjminecraft.doubleslabs.server.proxy.ServerProxy;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@Mod(DoubleSlabs.MODID)
public class DoubleSlabs {
    public static final String MODID = "doubleslabs";

    public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);

    private static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public DoubleSlabs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DSConfig.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DSConfig.CLIENT_SPEC);

        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forge = MinecraftForge.EVENT_BUS;


        mod.addListener(this::commonSetup);

        DSBlocks.BLOCKS.register(mod);
        DSTiles.TILES.register(mod);

        PROXY.addListeners(mod, forge);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
//        LOGGER.info(Arrays.toString(AnnotationUtil.getFieldInstances(SyncConfigValue.class, ForgeConfigSpec.ConfigValue.class).toArray()));
        DeferredWorkQueue.runLater(() -> {
            SlabSupport.init(); // TODO remove
            PacketHandler.registerPackets();
            PlayerConfigCapability.register();
        });
    }
}
