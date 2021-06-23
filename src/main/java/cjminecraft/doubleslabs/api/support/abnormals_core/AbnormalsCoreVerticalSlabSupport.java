package cjminecraft.doubleslabs.api.support.abnormals_core;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider(modid = "abnormals_core")
public class AbnormalsCoreVerticalSlabSupport extends VerticalSlabSupport {
    public AbnormalsCoreVerticalSlabSupport() {
        super("com.teamabnormals.abnormals_core.common.blocks.VerticalSlabBlock", "TYPE", "com.teamabnormals.abnormals_core.common.blocks.VerticalSlabBlock$VerticalSlabType");
    }
}
