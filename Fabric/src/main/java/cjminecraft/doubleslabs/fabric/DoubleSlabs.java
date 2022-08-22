package cjminecraft.doubleslabs.fabric;

import cjminecraft.doubleslabs.fabric.common.config.DSFabricConfig;
import net.fabricmc.api.ModInitializer;

public class DoubleSlabs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        DSFabricConfig.loadConfigs();
    }
}
