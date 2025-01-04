package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

import static cjminecraft.doubleslabs.common.Constants.id;

public class DSBlockEntities {

    public static final ResourceLocation DYNAMIC_SLAB_ID = id("dynamic_slab");

    // We have to wrap the supplier to ensure class loading gets the dynamic slab supplier
    public static final Supplier<BlockEntityType<? extends IDynamicSlabStateContainer>> DYNAMIC_SLAB = () -> Services.PLATFORM.getBlockEntityFactory().getDynamicSlabBlockEntity().get();

}
