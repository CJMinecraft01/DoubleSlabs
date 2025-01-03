package cjminecraft.doubleslabs.forge.common;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.forge.client.DoubleSlabsClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class DoubleSlabs {

    public DoubleSlabs() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> DoubleSlabsClient.addListeners(mod));

        DSForgeInit.register(mod);
        Internal.initialise();
    }
}