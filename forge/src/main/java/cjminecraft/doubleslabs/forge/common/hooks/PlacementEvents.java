package cjminecraft.doubleslabs.forge.common.hooks;

import cjminecraft.doubleslabs.common.hooks.PlacementHooks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlacementEvents {

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        final var result = PlacementHooks.useItemOnBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());

        if (result.isPresent()) {
            event.setCanceled(true);
            event.setCancellationResult(result.get());
        }
    }

}
