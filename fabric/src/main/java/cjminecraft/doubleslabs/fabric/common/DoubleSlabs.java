package cjminecraft.doubleslabs.fabric.common;

import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlockEntities;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;
import net.fabricmc.api.ModInitializer;

public class DoubleSlabs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        DSFabricBlocks.register();
        DSFabricBlockEntities.register();
        Internal.initialise();
    }
}
