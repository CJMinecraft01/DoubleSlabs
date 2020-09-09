package cjminecraft.doubleslabs.api.support.extendedmushrooms;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider(modid = "extendedmushrooms")
public class ExtendedMushroomsVerticalSlabSupport extends VerticalSlabSupport {
    public ExtendedMushroomsVerticalSlabSupport() {
        super("cech12.extendedmushrooms.block.VerticalSlabBlock", "TYPE", "cech12.extendedmushrooms.block.VerticalSlabBlock$VerticalSlabType");
    }
}
