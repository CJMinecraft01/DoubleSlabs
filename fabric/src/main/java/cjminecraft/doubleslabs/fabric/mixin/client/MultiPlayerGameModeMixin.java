package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import cjminecraft.doubleslabs.common.hooks.VerticalSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean setBlock(Level level, BlockPos pos, BlockState newState, int flags) {
        final var player = minecraft.player;

        if (!newState.isAir() || player == null || (player.isCreative() && !player.isCrouching())) {
            return level.setBlock(pos, newState, flags);
        }

        final var oldState = level.getBlockState(pos);

        if (oldState.is(DSBlocks.VERTICAL_SLAB.get())) {
            VerticalSlabBlockHooks.playerDestroy(player, level, pos, oldState, level.getBlockEntity(pos), player.getMainHandItem());
            return false;
        }

        return level.setBlock(pos, newState, flags);
    }

}
