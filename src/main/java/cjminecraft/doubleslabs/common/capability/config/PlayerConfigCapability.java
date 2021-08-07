package cjminecraft.doubleslabs.common.capability.config;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class PlayerConfigCapability {

    @CapabilityInject(IPlayerConfig.class)
    public static Capability<IPlayerConfig> PLAYER_CONFIG = null;

    public static final ResourceLocation PLAYER_CONFIG_RESOURCE_LOCATION = new ResourceLocation(DoubleSlabs.MODID, "player_config");

    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerConfig.class);
    }

}
