package cjminecraft.doubleslabs.test.common.init;

import cjminecraft.doubleslabs.common.util.registry.TileEntityRegistrar;
import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;

public class DSTTiles {

    public static final TileEntityRegistrar TILES = new TileEntityRegistrar(DoubleSlabsTest.MODID);

    public static final Class<ChestSlabTileEntity> CHEST = TILES.register("chest_slab", ChestSlabTileEntity.class);

}
