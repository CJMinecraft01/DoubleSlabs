package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSTiles {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DoubleSlabs.MODID);

    public static final RegistryObject<BlockEntityType<SlabTileEntity>> DYNAMIC_SLAB = BLOCK_ENTITY_TYPES.register("dynamic_slab", () -> BlockEntityType.Builder.of(SlabTileEntity::new, DSBlocks.DOUBLE_SLAB.get(), DSBlocks.VERTICAL_SLAB.get()).build(null));

    public static final RegistryObject<BlockEntityType<RaisedCampfireTileEntity>> CAMPFIRE = BLOCK_ENTITY_TYPES.register("campfire", () -> BlockEntityType.Builder.of(RaisedCampfireTileEntity::new, DSBlocks.RAISED_CAMPFIRE.get(), DSBlocks.RAISED_SOUL_CAMPFIRE.get()).build(null));

}
