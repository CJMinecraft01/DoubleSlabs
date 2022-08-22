package cjminecraft.doubleslabs.forge.common.capability.config;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.IPlayerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class PlayerConfigCapability {

    public static Capability<IPlayerConfig> PLAYER_CONFIG = CapabilityManager.get(new CapabilityToken<>(){});

    public static final ResourceLocation PLAYER_CONFIG_RESOURCE_LOCATION = new ResourceLocation(Constants.MODID, "player_config");

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IPlayerConfig.class);
    }

}
