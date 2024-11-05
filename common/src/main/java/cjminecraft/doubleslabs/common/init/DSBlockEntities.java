package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.platform.Services;
import cjminecraft.doubleslabs.registration.RegistrationProvider;
import cjminecraft.doubleslabs.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DSBlockEntities {

    private static final RegistrationProvider<BlockEntityType<?>> PROVIDER = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<?>, BlockEntityType<? extends IDynamicSlabStateContainer>> DYNAMIC_SLAB = PROVIDER.register("dynamic_slab", () -> BlockEntityType.Builder.of(Services.PLATFORM.getBlockEntityFactory()::createDynamicSlabBlockEntity, DSBlocks.DOUBLE_SLAB.get()).build(null));

    public static void loadClass() {

    }

}
