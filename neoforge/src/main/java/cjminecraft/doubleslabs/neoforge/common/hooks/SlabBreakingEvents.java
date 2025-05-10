package cjminecraft.doubleslabs.neoforge.common.hooks;

import cjminecraft.doubleslabs.common.hooks.DoubleSlabBlockHooks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class SlabBreakingEvents {

    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event) {
        if (DoubleSlabBlockHooks.trySeparateDoubleSlab(event.getPlayer(), event.getPlayer().level())) {
            event.setCanceled(true);
        }
    }

}
