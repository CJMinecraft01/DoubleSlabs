package cjminecraft.doubleslabs.forge.common;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.init.DSInit;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class DoubleSlabs {

    public DoubleSlabs() {
        Internal.initialise();
        DSInit.loadClasses();
    }
}