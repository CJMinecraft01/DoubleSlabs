package cjminecraft.doubleslabs.fabric.common.hooks;

import cjminecraft.doubleslabs.common.hooks.PlacementHooks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class PlacementEvents {

    public static void registerEvents() {
        UseBlockCallback.EVENT.register(PlacementEvents::rightClickBlock);
    }

    private static InteractionResult rightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        final var result = PlacementHooks.useItemOnBlock(player, level, hand, hitResult);

        return result.orElse(InteractionResult.PASS);
    }

}
