package cjminecraft.doubleslabs.forge.common.events;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.placement.PlacementHandler;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class PlayerEvents {

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = PlacementHandler.onItemUse(event.getLevel(), event.getEntity(), event.getFace(), event.getPos(), event.getItemStack(), event.getHand());
        if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

}
