package cjminecraft.doubleslabs.api.support.quark;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider(modid = "quark")
public class QuarkSlabSupport extends VerticalSlabSupport {
    public QuarkSlabSupport() {
        super("vazkii.quark.content.building.block.VerticalSlabBlock", "TYPE", "vazkii.quark.content.building.block.VerticalSlabBlock$VerticalSlabType");
    }
}
