package cjminecraft.doubleslabs.neoforge.hooks;

import cjminecraft.doubleslabs.client.hooks.ClientRenderingHooks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

@EventBusSubscriber
public class ClientRenderingEvents {

    @SubscribeEvent
    public static void addTextToDebugScreenOverlay(CustomizeGuiOverlayEvent.DebugText event) {
        ClientRenderingHooks.addTextToDebugScreenOverlay(event.getRight());
    }

}
