package cjminecraft.doubleslabs.api.support.abnormals_core;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider
public class AbnormalsCoreSlabSupport extends VerticalSlabSupport {

    public AbnormalsCoreSlabSupport() {
        super("com.minecraftabnormals.abnormals_core.common.blocks.VerticalSlabBlock", "TYPE", "com.minecraftabnormals.abnormals_core.common.blocks.VerticalSlabBlock$VerticalSlabType");
    }
}
