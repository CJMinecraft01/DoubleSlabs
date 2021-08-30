package cjminecraft.doubleslabs.api.support.infernalexp;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider(modid = "infernalexp")
public class InfernalExpansionSlabSupport extends VerticalSlabSupport {

    public InfernalExpansionSlabSupport() {
        super("org.infernalstudios.infernalexp.blocks.VerticalSlabBlock", "TYPE", "org.infernalstudios.infernalexp.blocks.VerticalSlabBlock$VerticalSlabType");
    }
}
