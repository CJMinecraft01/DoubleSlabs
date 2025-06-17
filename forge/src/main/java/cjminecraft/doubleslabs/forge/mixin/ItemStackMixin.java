package cjminecraft.doubleslabs.forge.mixin;

import cjminecraft.doubleslabs.common.hooks.PlacementHooks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        final var result = PlacementHooks.useItemOnBlock(context.getPlayer(), context.getLevel(), context.getHand(), new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), true));

        result.ifPresent(cir::setReturnValue);
    }

}
