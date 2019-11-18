package cjminecraft.doubleslabs.waila;

import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class PluginDoubleSlabs implements IWailaPlugin {
    @Override
    public void register(IWailaRegistrar registrar) {
        registrar.registerStackProvider(HUDHandlerDoubleSlab.INSTANCE, BlockDoubleSlab.class);
    }
}
