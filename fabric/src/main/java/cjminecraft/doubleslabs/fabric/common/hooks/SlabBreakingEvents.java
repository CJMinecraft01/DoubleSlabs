package cjminecraft.doubleslabs.fabric.common.hooks;

import cjminecraft.doubleslabs.common.hooks.DoubleSlabBlockHooks;
import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import cjminecraft.doubleslabs.common.hooks.VerticalSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SlabBreakingEvents {

    public static void registerEvents() {
        PlayerBlockBreakEvents.BEFORE.register(SlabBreakingEvents::breakBlock);
    }

    private static boolean breakBlock(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        final var willHarvest = !player.isCreative() && player.hasCorrectToolForDrops(state);

        if (state.is(DSBlocks.MIXED_SLABS)) {
            return willHarvest || !MixedDoubleSlabBlockHooks.removeBlock(state, level, pos, player, false);
        }

        if (state.is(DSBlocks.VERTICAL_SLAB.get())) {
            return willHarvest || !VerticalSlabBlockHooks.removeBlock(state, level, pos, player, false);
        }

        return !DoubleSlabBlockHooks.trySeparateDoubleSlab(player, level);
    }

}
