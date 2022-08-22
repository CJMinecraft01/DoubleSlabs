package cjminecraft.doubleslabs.forge;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.forge.common.config.DSForgeConfig;
import cjminecraft.doubleslabs.forge.common.init.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MODID)
public class DoubleSlabs {
    
    public DoubleSlabs() {
        DSForgeConfig.registerConfigs();

        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        DSRegistries.HORIZONTAL_SLAB_SUPPORTS.register(mod);
        DSRegistries.VERTICAL_SLAB_SUPPORTS.register(mod);
        DSRegistries.CONTAINER_SUPPORTS.register(mod);

        DSBlockEntities.BLOCK_ENTITY_TYPES.register(mod);
        DSMenuTypes.MENU_TYPES.register(mod);
        DSBlocks.BLOCKS.register(mod);
        DSItems.ITEMS.register(mod);

        mod.addListener(DSForgeConfig::onFileChange);
    }
}