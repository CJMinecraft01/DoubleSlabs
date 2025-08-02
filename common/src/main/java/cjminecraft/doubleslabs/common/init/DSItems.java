package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.item.VerticalSlabItem;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import cjminecraft.doubleslabs.common.platform.Services;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static cjminecraft.doubleslabs.common.Constants.id;

public class DSItems {

    public static final ResourceLocation VERTICAL_SLAB_ID = id("vertical_slab");

    public static final Supplier<VerticalSlabItem> VERTICAL_SLAB = () -> Services.PLATFORM.getItems().getVerticalSlabItem();

    public static final ResourceLocation VERTICAL_SLAB_CONTENT_ID = id("vertical_slab_content");

    public static final Supplier<DataComponentType<VerticalSlabContent>> VERTICAL_SLAB_CONTENT = () -> Services.PLATFORM.getItems().getVerticalSlabContentDataComponentType();

}
