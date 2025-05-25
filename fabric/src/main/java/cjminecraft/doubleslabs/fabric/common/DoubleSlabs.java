package cjminecraft.doubleslabs.fabric.common;

import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.fabric.common.hooks.SlabBreakingEvents;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlockEntities;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricItems;
import net.fabricmc.api.ModInitializer;

public class DoubleSlabs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        DSFabricBlocks.register();
        DSFabricItems.register();
        DSFabricBlockEntities.register();

        SlabBreakingEvents.registerEvents();

        Internal.initialise();
    }
}
