package cjminecraft.doubleslabs.neoforge.common;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class DoubleSlabs {

    public DoubleSlabs(IEventBus modBus) {
        DSNeoForgeInit.register(modBus);
        Internal.initialise();
    }
}