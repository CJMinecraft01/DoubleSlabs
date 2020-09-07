package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import cjminecraft.doubleslabs.common.tileentity.SlabConverterTileEntity;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSTiles {

    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DoubleSlabs.MODID);

    public static final RegistryObject<TileEntityType<SlabTileEntity>> DYNAMIC_SLAB = TILES.register("dynamic_slab", () -> TileEntityType.Builder.create(SlabTileEntity::new, DSBlocks.DOUBLE_SLAB.get(), DSBlocks.VERTICAL_SLAB.get()).build(null));
    public static final RegistryObject<TileEntityType<SlabConverterTileEntity>> DOUBLE_SLAB = TILES.register("double_slabs", () -> TileEntityType.Builder.create(() -> new SlabConverterTileEntity(DSTiles.DOUBLE_SLAB.get()), DSBlocks.DOUBLE_SLAB.get()).build(null));
    public static final RegistryObject<TileEntityType<SlabConverterTileEntity>> VERTICAL_SLAB = TILES.register("vertical_slab", () -> TileEntityType.Builder.create(() -> new SlabConverterTileEntity(DSTiles.VERTICAL_SLAB.get()), DSBlocks.VERTICAL_SLAB.get()).build(null));

    public static final RegistryObject<TileEntityType<RaisedCampfireTileEntity>> CAMPFIRE = TILES.register("campfire", () -> TileEntityType.Builder.create(RaisedCampfireTileEntity::new, DSBlocks.RAISED_CAMPFIRE.get()).build(null));

}
