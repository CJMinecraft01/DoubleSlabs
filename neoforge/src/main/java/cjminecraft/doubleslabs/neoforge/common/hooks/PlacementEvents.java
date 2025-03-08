package cjminecraft.doubleslabs.neoforge.common.hooks;

import cjminecraft.doubleslabs.common.hooks.PlacementHooks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber
public class PlacementEvents {

    @SubscribeEvent
    public static void useItemOnBlock(UseItemOnBlockEvent event) {
        // We only want to handle when we are about to place a block
        if (event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK) {
            final var result = PlacementHooks.useItemOnBlock(event.getUseOnContext());
            if (result.isPresent()) {
                event.setCanceled(true);
                event.setCancellationResult(result.get());
            }
        }
    }

}
