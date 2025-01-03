package cjminecraft.doubleslabs.fabric.common;

import cjminecraft.doubleslabs.common.Internal;
import net.fabricmc.api.ModInitializer;

public class DoubleSlabs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        DSFabricInit.register();
        Internal.initialise();
    }
}
