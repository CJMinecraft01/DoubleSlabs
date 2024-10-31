package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.platform.Services;
import cjminecraft.doubleslabs.registration.RegistrationProvider;
import cjminecraft.doubleslabs.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public class DSBlocks {

    private static final RegistrationProvider<Block> PROVIDER = RegistrationProvider.get(Registries.BLOCK, Constants.MOD_ID);

    public static final RegistryObject<Block, MixedDoubleSlabBlock> DOUBLE_SLAB = PROVIDER.register("double_slab",
            () -> Services.PLATFORM.getBlockFactory().createMixedDoubleSlabBlock());

    public static void loadClass() {

    }

}
