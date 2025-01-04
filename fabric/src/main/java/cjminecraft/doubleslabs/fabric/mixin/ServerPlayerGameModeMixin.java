package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import cjminecraft.doubleslabs.fabric.common.init.DSFabricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Shadow @Final protected ServerPlayer player;

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean destroyBlock$removeBlock$doubleslabs(ServerLevel level, BlockPos pos, boolean isMoving) {
        BlockState state = level.getBlockState(pos);

        if (state.is(DSFabricBlocks.MIXED_SLAB)) {
            return MixedDoubleSlabBlockHooks.removeBlock(state, level, pos, player, level.getFluidState(pos), !player.isCreative());
        }

        return level.removeBlock(pos, isMoving);
    }

}
