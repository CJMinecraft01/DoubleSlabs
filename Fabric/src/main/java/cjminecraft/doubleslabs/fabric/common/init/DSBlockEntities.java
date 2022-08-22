package cjminecraft.doubleslabs.fabric.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.common.init.IBlockEntities;
import cjminecraft.doubleslabs.fabric.common.block.entity.FabricSlabBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DSBlockEntities implements IBlockEntities {

    public static final IBlockEntities INSTANCE = new DSBlockEntities();

    // todo: change leaves to slab types
    public static BlockEntityType<FabricSlabBlockEntity> DYNAMIC_SLAB;

    public static void register() {
        DYNAMIC_SLAB = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Constants.MODID, "dynamic_slab"),
                FabricBlockEntityTypeBuilder.create(FabricSlabBlockEntity::new, Blocks.ACACIA_LEAVES).build()
        );
    }

    @Override
    public BlockEntityType<? extends SlabBlockEntity<?>> dynamicSlab() {
        return DYNAMIC_SLAB;
    }

    @Override
    public SlabBlockEntity<?> createSlabBlockEntity(BlockPos pos, BlockState state) {
        return new FabricSlabBlockEntity(pos, state);
    }
}
