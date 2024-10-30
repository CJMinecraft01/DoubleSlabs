package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.DynamicDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.Services;
import cjminecraft.doubleslabs.registration.RegistrationProvider;
import cjminecraft.doubleslabs.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public class DSBlocks {

    private static final RegistrationProvider<Block> PROVIDER = RegistrationProvider.get(Registries.BLOCK, Constants.MOD_ID);

    public static final RegistryObject<Block, DynamicDoubleSlabBlock> DOUBLE_SLAB = PROVIDER.register("double_slab", () -> Services.PLATFORM.getBlockFactory().createDynamicDoubleSlabBlock());

    public static void loadClass() {

    }

}
