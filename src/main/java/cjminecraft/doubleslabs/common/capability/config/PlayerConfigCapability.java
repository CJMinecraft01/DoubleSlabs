package cjminecraft.doubleslabs.common.capability.config;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class PlayerConfigCapability {

    @CapabilityInject(IPlayerConfig.class)
    public static Capability<IPlayerConfig> PLAYER_CONFIG = null;

}
