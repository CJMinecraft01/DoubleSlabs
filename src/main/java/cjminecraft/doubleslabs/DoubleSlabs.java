package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.patches.DynamicSurroundings;
import cjminecraft.doubleslabs.proxy.IProxy;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = DoubleSlabs.NAME, version = DoubleSlabs.VERSION, modid = DoubleSlabs.MODID, acceptedMinecraftVersions = DoubleSlabs.ACCEPTED_MC_VERSIONS, updateJSON = DoubleSlabs.UPDATE_URL, certificateFingerprint = DoubleSlabs.CERTIFICATE_FINGERPRINT)
public class DoubleSlabs
{
    public static final String NAME = "DoubleSlabs";
    public static final String MODID = "doubleslabs";
    public static final String VERSION = "${version}";
    public static final String ACCEPTED_MC_VERSIONS = "[1.12,1.12.2]";
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/CJMinecraft01/DoubleSlabs/1.12.x/update.json";
    public static final Logger LOGGER = LogManager.getFormatterLogger(NAME);
    public static final String CERTIFICATE_FINGERPRINT = "${fingerprint}";

    @SidedProxy(serverSide = "cjminecraft.doubleslabs.proxy.ServerProxy", clientSide = "cjminecraft.doubleslabs.proxy.ClientProxy")
    private static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ConfigManager.sync(MODID, Config.Type.INSTANCE);
        DynamicSurroundings.prepare();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
        SlabSupport.init();
        ContainerSupport.init();
    }

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

    @Mod.EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (!message.isStringMessage()) continue;

            if (message.key.equalsIgnoreCase("register")) {
                LOGGER.info("Received slab support registration from [{}] for class {}", message.getSender(), message.getStringValue());
                SlabSupport.addSupportFromIMC(message.getStringValue());
            }
        }
    }
}
