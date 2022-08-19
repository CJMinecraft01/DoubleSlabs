package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.fabric.common.block.entity.FabricSlabBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DSBlockEntities {

    // todo: change leaves to slab types
    public static final BlockEntityType<FabricSlabBlockEntity> DYNAMIC_SLAB = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new ResourceLocation(Constants.MODID, "dynamic_slab"),
            FabricBlockEntityTypeBuilder.create(FabricSlabBlockEntity::new, Blocks.ACACIA_LEAVES).build()
    );

}
