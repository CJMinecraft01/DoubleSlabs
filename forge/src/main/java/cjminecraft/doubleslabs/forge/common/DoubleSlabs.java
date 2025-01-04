package cjminecraft.doubleslabs.forge.common;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.forge.client.DoubleSlabsClient;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlockEntities;
import cjminecraft.doubleslabs.forge.common.init.DSForgeBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class DoubleSlabs {

    public DoubleSlabs(ModLoadingContext context) {
        IEventBus modBus = context.<FMLJavaModLoadingContext>extension().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> DoubleSlabsClient.addListeners(modBus));

        DSForgeBlocks.BLOCKS.register(modBus);
        DSForgeBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
        Internal.initialise();
    }
}