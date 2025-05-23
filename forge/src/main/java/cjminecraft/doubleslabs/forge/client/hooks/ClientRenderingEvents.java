package cjminecraft.doubleslabs.forge.client.hooks;

import cjminecraft.doubleslabs.client.hooks.ClientRenderingHooks;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientRenderingEvents {

    @SubscribeEvent
    public static void renderBlockHighlight(RenderHighlightEvent.Block event) {
        if (ClientRenderingHooks.renderBlockHighlight(event.getPoseStack(), event.getCamera().getPosition().x, event.getCamera().getPosition().y, event.getCamera().getPosition().z, () -> event.getMultiBufferSource().getBuffer(RenderType.lines()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void addTextToDebugScreenOverlay(CustomizeGuiOverlayEvent.DebugText event) {
        if (event.getSide() == CustomizeGuiOverlayEvent.DebugText.Side.Right) {
            ClientRenderingHooks.addTextToDebugScreenOverlay(event.getText());
        }
    }

}
