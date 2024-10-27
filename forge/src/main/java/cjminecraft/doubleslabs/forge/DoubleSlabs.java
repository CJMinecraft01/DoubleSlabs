package cjminecraft.doubleslabs.forge;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class DoubleSlabs {

    public DoubleSlabs() {
        Internal.initialise();
    }
}