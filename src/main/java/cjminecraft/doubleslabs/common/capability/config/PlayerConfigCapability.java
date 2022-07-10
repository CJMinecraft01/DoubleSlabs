package cjminecraft.doubleslabs.common.capability.config;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class PlayerConfigCapability {

    public static Capability<IPlayerConfig> PLAYER_CONFIG = CapabilityManager.get(new CapabilityToken<>(){});

    public static final ResourceLocation PLAYER_CONFIG_RESOURCE_LOCATION = new ResourceLocation(DoubleSlabs.MODID, "player_config");

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IPlayerConfig.class);
    }

}
