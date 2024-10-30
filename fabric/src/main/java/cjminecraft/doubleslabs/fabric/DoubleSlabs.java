package cjminecraft.doubleslabs.fabric;

import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.init.DSInit;
import net.fabricmc.api.ModInitializer;

public class DoubleSlabs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Internal.initialise();
        DSInit.loadClasses();
    }
}
