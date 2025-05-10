package cjminecraft.doubleslabs.forge.common.hooks;

import cjminecraft.doubleslabs.common.hooks.DoubleSlabBlockHooks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SlabBreakingEvents {

    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event) {
        if (DoubleSlabBlockHooks.trySeparateDoubleSlab(event.getPlayer(), event.getPlayer().level())) {
            event.setCanceled(true);
        }
    }

}
