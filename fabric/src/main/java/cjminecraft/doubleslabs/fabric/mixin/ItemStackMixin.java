package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.hooks.PlacementHooks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void useOn$doubleslabs(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Optional<ItemInteractionResult> itemInteractionResult = PlacementHooks.useItemOnBlock(context);
        itemInteractionResult.ifPresent(interactionResult -> cir.setReturnValue(interactionResult.result()));
    }

}
