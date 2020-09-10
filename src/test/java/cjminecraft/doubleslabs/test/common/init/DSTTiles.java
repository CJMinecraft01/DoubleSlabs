package cjminecraft.doubleslabs.test.common.init;

import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DSTTiles {

    public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, DoubleSlabsTest.MODID);

    public static final RegistryObject<TileEntityType<ChestSlabTileEntity>> CHEST = TILES.register("chest_slab", () -> TileEntityType.Builder.create(ChestSlabTileEntity::new, DSTBlocks.CHEST_SLAB.get()).build(null));

}
