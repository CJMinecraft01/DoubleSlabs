package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.placement.PlacementHandler;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(method = "performUseItemOn", at = @At("HEAD"), cancellable = true)
    public void performUseItemOn(LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = PlacementHandler.onItemUse(localPlayer.level, localPlayer, blockHitResult.getDirection(), blockHitResult.getBlockPos(), localPlayer.getItemInHand(interactionHand), interactionHand);
        if (result.consumesAction())
            cir.setReturnValue(result);
    }

}
