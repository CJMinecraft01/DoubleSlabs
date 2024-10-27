package cjminecraft.doubleslabs.fabric;

import cjminecraft.doubleslabs.common.Internal;
import net.fabricmc.api.ModInitializer;

public class DoubleSlabs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Internal.initialise();
    }
}
