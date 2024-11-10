package cjminecraft.doubleslabs.forge.client.hooks;

import cjminecraft.doubleslabs.client.hooks.ClientRenderingHooks;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClientRenderingEvents {

    @SubscribeEvent
    public static void addTextToDebugScreenOverlay(CustomizeGuiOverlayEvent.DebugText event) {
        if (event.getSide() == CustomizeGuiOverlayEvent.DebugText.Side.Right) {
            ClientRenderingHooks.addTextToDebugScreenOverlay(event.getText());
        }
    }

}
