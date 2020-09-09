package cjminecraft.doubleslabs.api.support.lottaterracotta;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.VerticalSlabSupport;

@SlabSupportProvider(modid = "lottaterracotta")
public class LottaTerracottaVerticalSlabSupport extends VerticalSlabSupport {
    public LottaTerracottaVerticalSlabSupport() {
        super("org.villainy.lottaterracotta.blocks.VerticalSlabBlock", "TYPE", "org.villainy.lottaterracotta.blocks.VerticalSlabBlock$VerticalSlabType");
    }
}
