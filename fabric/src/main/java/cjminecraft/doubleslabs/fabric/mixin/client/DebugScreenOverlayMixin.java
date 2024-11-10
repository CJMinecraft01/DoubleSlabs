package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.client.hooks.ClientRenderingHooks;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    @Inject(method = "getSystemInformation", at = @At("RETURN"), cancellable = true)
    private void getSystemInformation$doubleslabs(CallbackInfoReturnable<List<String>> cir) {
        List<String> text = cir.getReturnValue();
        ClientRenderingHooks.addTextToDebugScreenOverlay(text);
        cir.setReturnValue(text);
    }

}
