package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean destroyBlock$setBlock$doubleslabs(Level level, BlockPos pos, BlockState newState, int flags) {
        final var state = level.getBlockState(pos);

        if (state.is(DSFabricBlocks.MIXED_SLAB)) {
            assert minecraft.player != null;
            if (MixedDoubleSlabBlockHooks.removeBlock(state, level, pos, minecraft.player, level.getFluidState(pos), false)) {
                return true;
            }
        }

        return level.setBlock(pos, newState, flags);
    }
}
