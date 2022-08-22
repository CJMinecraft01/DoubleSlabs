package cjminecraft.doubleslabs.fabric;

import cjminecraft.doubleslabs.fabric.common.config.DSFabricConfig;
import cjminecraft.doubleslabs.fabric.common.init.*;
import net.fabricmc.api.ModInitializer;

public class DoubleSlabs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        DSRegistries.register();

        DSBlocks.register();
        DSItems.register();
        DSBlockEntities.register();
        DSMenuTypes.register();
        DSTabs.register();

        DSFabricConfig.loadConfigs();
    }
}
