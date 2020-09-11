package cjminecraft.doubleslabs.api.support.swampexpansion;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider(modid = "swampexpansion")
public class SwampExpansionSupport extends VerticalSlabSupport {
    public SwampExpansionSupport() {
        super("com.farcr.swampexpansion.common.block", "TYPE", "com.farcr.swampexpansion.common.block$VerticalSlabType");
    }
}
