package cjminecraft.doubleslabs.test.common.init;

import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSTTiles {

    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, DoubleSlabsTest.MODID);

    public static final RegistryObject<BlockEntityType<ChestSlabTileEntity>> CHEST = TILES.register("chest_slab", () -> BlockEntityType.Builder.of(ChestSlabTileEntity::new, DSTBlocks.CHEST_SLAB.get()).build(null));

}
