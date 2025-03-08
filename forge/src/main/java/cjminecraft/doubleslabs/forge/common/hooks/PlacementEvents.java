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
        final var context = new UseOnContext(event.getLevel(), event.getEntity(), event.getHand(),
                event.getItemStack(), event.getHitVec());

        final var result = PlacementHooks.useItemOnBlock(context);
        if (result.isPresent()) {
            event.setCanceled(true);
            event.setCancellationResult(result.get());
        }
    }

}
