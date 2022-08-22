package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.config.PlayerConfig;
import cjminecraft.doubleslabs.fabric.api.extensions.IPlayerExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements IPlayerExtensions {

    private final PlayerConfig config = new PlayerConfig();

    @Override
    public IPlayerConfig getPlayerConfig() {
        return config;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readAdditionalData(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("doubleslabs_player_config"))
            this.config.deserializeNBT(nbt.getCompound("doubleslabs_player_config"));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addAdditionalData(CompoundTag nbt, CallbackInfo ci) {
        nbt.put("doubleslabs_player_config", this.config.serializeNBT());
    }
}
