package cjminecraft.doubleslabs.forge;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.forge.client.proxy.ClientProxy;
import cjminecraft.doubleslabs.forge.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.forge.common.init.*;
import cjminecraft.doubleslabs.forge.common.proxy.IProxy;
import cjminecraft.doubleslabs.forge.server.proxy.ServerProxy;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MODID)
public class DoubleSlabs {

    public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    
    public DoubleSlabs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DSConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DSConfig.COMMON_SPEC);

        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        DSRegistries.HORIZONTAL_SLAB_SUPPORTS.register(mod);
        DSRegistries.VERTICAL_SLAB_SUPPORTS.register(mod);
        DSRegistries.CONTAINER_SUPPORTS.register(mod);

        mod.addListener(this::commonSetup);
        mod.addListener(this::registerCapabilities);

        DSBlockEntities.BLOCK_ENTITY_TYPES.register(mod);
        DSMenuTypes.MENU_TYPES.register(mod);
        DSBlocks.BLOCKS.register(mod);
        DSItems.ITEMS.register(mod);

        PROXY.addListeners(mod, MinecraftForge.EVENT_BUS);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Services.NETWORK.init();
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        PlayerConfigCapability.register(event);
    }
}