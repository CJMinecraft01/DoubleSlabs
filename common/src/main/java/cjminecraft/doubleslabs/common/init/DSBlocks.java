package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import static cjminecraft.doubleslabs.common.Constants.id;

public class DSBlocks {

    public static final ResourceLocation MIXED_SLAB_ID = id("mixed_slab");
    public static final ResourceLocation TRANSPARENT_MIXED_SLAB_ID = id("transparent_mixed_slab");

    public static final BlockBehaviour.Properties MIXED_SLAB_PROPERTIES = BlockBehaviour.Properties.of();
    public static final BlockBehaviour.Properties TRANSPARENT_MIXED_SLAB_PROPERTIES = BlockBehaviour.Properties.of().noOcclusion();

    // We have to wrap the supplier to ensure class loading gets the mixed slab supplier
    public static final Supplier<MixedDoubleSlabBlock> MIXED_SLAB = () -> Services.PLATFORM.getBlocks().getMixedSlabBlock();
    public static final Supplier<MixedDoubleSlabBlock> TRANSPARENT_MIXED_SLAB = () -> Services.PLATFORM.getBlocks().getTransparentMixedSlabBlock();

    public static final TagKey<Block> MIXED_SLABS = TagKey.create(Registries.BLOCK, id("mixed_slabs"));

}
