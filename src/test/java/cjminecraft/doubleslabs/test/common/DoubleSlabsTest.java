package cjminecraft.doubleslabs.test.common;

import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.test.common.container.GuiHandler;
import cjminecraft.doubleslabs.test.common.init.DSTBlocks;
import cjminecraft.doubleslabs.test.common.init.DSTTiles;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = DoubleSlabsTest.MODID, name = DoubleSlabsTest.NAME, useMetadata = true)
public class DoubleSlabsTest {

    public static final String MODID = "doubleslabstest";
    public static final String NAME = "DoubleSlabsTest";

    @Mod.Instance(MODID)
    public static DoubleSlabsTest instance;

    @SidedProxy(serverSide = "cjminecraft.doubleslabs.test.server.proxy.ServerProxy", clientSide = "cjminecraft.doubleslabs.test.client.proxy.ClientProxy")
    public static IProxy PROXY;

    public DoubleSlabsTest() {
        DSTBlocks.BLOCKS.register(MinecraftForge.EVENT_BUS);
        DSTBlocks.ITEMS.register(MinecraftForge.EVENT_BUS);
        DSTTiles.TILES.register(MinecraftForge.EVENT_BUS);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        PROXY.preInit();
    }

}
