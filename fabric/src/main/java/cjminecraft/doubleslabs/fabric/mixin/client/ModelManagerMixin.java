package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.fabric.client.DoubleSlabsClient;
import net.minecraft.client.resources.model.*;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
    private void onModelBaked(ModelManager.ReloadState reloadState, ProfilerFiller profiler, CallbackInfo ci) {
        DoubleSlabsClient.bakeVerticalSlabs(reloadState.modelBakery(), (ModelManager) (Object) this);
    }
}
