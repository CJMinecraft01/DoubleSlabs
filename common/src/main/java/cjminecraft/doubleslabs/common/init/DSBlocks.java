package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import static cjminecraft.doubleslabs.common.Constants.id;

public class DSBlocks {

    public static final ResourceLocation MIXED_SLAB_ID = id("mixed_slab");

    public static final BlockBehaviour.Properties MIXED_SLAB_PROPERTIES = BlockBehaviour.Properties.of();

    // We have to wrap the supplier to ensure class loading gets the mixed slab supplier
    public static final Supplier<MixedDoubleSlabBlock> MIXED_SLAB = () -> Services.PLATFORM.getBlocks().getMixedSlabBlock();

}
