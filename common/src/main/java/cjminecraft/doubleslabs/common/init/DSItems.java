package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.platform.Services;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static cjminecraft.doubleslabs.common.Constants.id;

public class DSItems {

    public static final ResourceLocation VERTICAL_SLAB_ID = id("vertical_slab");

    public static final Supplier<VerticalSlabItem> VERTICAL_SLAB = () -> Services.PLATFORM.getItems().getVerticalSlabItem();

}
