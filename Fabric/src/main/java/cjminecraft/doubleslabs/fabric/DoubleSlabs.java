package cjminecraft.doubleslabs.fabric;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.fabric.common.init.*;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class DoubleSlabs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ModLoadingContext.registerConfig(Constants.MODID, ModConfig.Type.CLIENT, DSConfig.CLIENT_SPEC);
        ModLoadingContext.registerConfig(Constants.MODID, ModConfig.Type.COMMON, DSConfig.COMMON_SPEC);

        DSRegistries.register();

        DSBlocks.register();
        DSItems.register();
        DSBlockEntities.register();
        DSMenuTypes.register();
        DSTabs.register();
    }
}
