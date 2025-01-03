package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Unique
    private SoundType getSoundType(BlockState state, BlockPos pos, Player player) {
        if (state.is(DSBlocks.MIXED_SLAB)) {
            return MixedDoubleSlabBlockHooks.getSoundType(player.level(), pos, player).orElseGet(state::getSoundType);
        }
        return state.getSoundType();
    }

    @Inject(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;playCombinationStepSounds(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V"), cancellable = true)
    private void playStepSound$playCombinationStepSounds(BlockPos pos, BlockState state, CallbackInfo ci, @Local(ordinal = 1) BlockPos blockPos, @Local(ordinal = 1) BlockState blockState) {
        Player player = (Player) (Object) this;

        SoundType primarySoundType = getSoundType(blockState, blockPos, player);
        playSound(primarySoundType.getStepSound(), primarySoundType.getVolume() * 0.15F, primarySoundType.getPitch());

        SoundType secondarySoundType = getSoundType(state, pos, player);
        playSound(secondarySoundType.getStepSound(), secondarySoundType.getVolume() * 0.05F, secondarySoundType.getPitch() * 0.8F);

        ci.cancel();
    }

    @Inject(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;playMuffledStepSound(Lnet/minecraft/world/level/block/state/BlockState;)V"), cancellable = true)
    private void playStepSound$playCombinationStepSounds(BlockPos pos, BlockState state, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        SoundType soundType = getSoundType(state, pos, player);
        playSound(soundType.getStepSound(), soundType.getVolume() * 0.05F, soundType.getPitch() * 0.8F);

        ci.cancel();
    }

}
