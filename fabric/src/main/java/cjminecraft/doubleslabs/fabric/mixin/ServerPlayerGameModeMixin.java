package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.hooks.VerticalSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Shadow @Final protected ServerPlayer player;

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean removeBlock(ServerLevel instance, BlockPos pos, boolean isMoving) {
        final var oldState = instance.getBlockState(pos);

        if (oldState.is(DSBlocks.VERTICAL_SLAB.get()) && VerticalSlabBlockHooks.removeBlock(oldState, instance, pos, player, false)) {
            return true;
        }

        return instance.removeBlock(pos, false);
    }

}
