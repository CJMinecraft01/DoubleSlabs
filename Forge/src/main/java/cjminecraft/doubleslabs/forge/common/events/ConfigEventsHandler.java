package cjminecraft.doubleslabs.forge.common.events;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.forge.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.forge.common.capability.config.PlayerConfigContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class ConfigEventsHandler {

    @SubscribeEvent
    public static void attachConfigCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player)
            event.addCapability(PlayerConfigCapability.PLAYER_CONFIG_RESOURCE_LOCATION, new PlayerConfigContainer());
    }

    // todo: player config packets

}
