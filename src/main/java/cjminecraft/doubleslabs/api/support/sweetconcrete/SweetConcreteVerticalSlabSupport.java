package cjminecraft.doubleslabs.api.support.sweetconcrete;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider(modid = "sweetconcrete")
public class SweetConcreteVerticalSlabSupport extends VerticalSlabSupport {

    public SweetConcreteVerticalSlabSupport() {
        super("org.villainy.sweetconcrete.blocks.VerticalSlabBlock", "TYPE", "org.villainy.sweetconcrete.blocks.VerticalSlabBlock$VerticalSlabType");
    }

}
