package cjminecraft.doubleslabs.neoforge;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.init.DSInit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class DoubleSlabs {

    public DoubleSlabs(IEventBus eventBus) {
        Internal.initialise();
        DSInit.loadClasses();
    }
}