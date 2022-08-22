package cjminecraft.doubleslabs.forge;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.forge.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.forge.common.init.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MODID)
public class DoubleSlabs {
    
    public DoubleSlabs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DSConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DSConfig.COMMON_SPEC);

        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        DSRegistries.HORIZONTAL_SLAB_SUPPORTS.register(mod);
        DSRegistries.VERTICAL_SLAB_SUPPORTS.register(mod);
        DSRegistries.CONTAINER_SUPPORTS.register(mod);

        mod.addListener(this::registerCapabilities);

        DSBlockEntities.BLOCK_ENTITY_TYPES.register(mod);
        DSMenuTypes.MENU_TYPES.register(mod);
        DSBlocks.BLOCKS.register(mod);
        DSItems.ITEMS.register(mod);
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        PlayerConfigCapability.register(event);
    }
}