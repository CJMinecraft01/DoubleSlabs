package cjminecraft.doubleslabs.forge.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.forge.common.block.entity.ForgeSlabBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DSBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MODID);

    // todo: change leaves to slab types
    public static final RegistryObject<BlockEntityType<ForgeSlabBlockEntity>> DYNAMIC_SLAB = BLOCK_ENTITY_TYPES.register("dynamic_slab", () -> BlockEntityType.Builder.of(ForgeSlabBlockEntity::new, Blocks.ACACIA_LEAVES).build(null));

}
