package cjminecraft.doubleslabs.api.support.worldofwonder;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider(modid = "worldofwonder")
public class WorldOfWonderVerticalSlabSupport extends VerticalSlabSupport {
    public WorldOfWonderVerticalSlabSupport() {
        super("net.msrandom.worldofwonder.block.VerticalSlabBlock", "TYPE", "net.msrandom.worldofwonder.block.VerticalSlabBlock$VerticalSlabType");
    }
}
