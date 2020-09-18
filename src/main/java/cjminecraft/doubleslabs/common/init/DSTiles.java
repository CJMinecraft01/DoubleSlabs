package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.tileentity.SlabConverterTileEntity;
import cjminecraft.doubleslabs.common.tileentity.SlabConverterTileEntity2;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.registry.TileEntityRegistrar;

public class DSTiles {

    public static final TileEntityRegistrar TILES = new TileEntityRegistrar(DoubleSlabs.MODID);

    public static final Class<SlabTileEntity> DYNAMIC_SLAB = TILES.register("dynamic_slab", SlabTileEntity.class);
    public static final Class<SlabConverterTileEntity> DOUBLE_SLAB = TILES.register("double_slab", SlabConverterTileEntity.class);
    public static final Class<SlabConverterTileEntity2> VERTICAL_SLAB = TILES.register("vertical_slab", SlabConverterTileEntity2.class);

}
