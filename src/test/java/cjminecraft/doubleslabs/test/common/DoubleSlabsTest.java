package cjminecraft.doubleslabs.test.common;

import cjminecraft.doubleslabs.test.client.proxy.ClientProxy;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.test.common.init.DSTContainers;
import cjminecraft.doubleslabs.test.common.init.DSTTiles;
import cjminecraft.doubleslabs.test.server.proxy.ServerProxy;
import cjminecraft.doubleslabs.test.common.init.DSTBlocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DoubleSlabsTest.MODID)
public class DoubleSlabsTest {
    public static final String MODID = "doubleslabstest";

    private static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public DoubleSlabsTest() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forge = MinecraftForge.EVENT_BUS;

        DSTBlocks.BLOCKS.register(mod);
        DSTBlocks.ITEMS.register(mod);
        DSTTiles.BLOCK_ENTITY_TYPES.register(mod);
        DSTContainers.MENU_TYPES.register(mod);

        PROXY.addListeners(mod, forge);
    }
}
