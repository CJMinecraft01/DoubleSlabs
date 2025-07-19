package cjminecraft.doubleslabs.forge.common.block;

import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.hooks.VerticalSlabBlockHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class ForgeVerticalSlabBlock extends VerticalSlabBlock {
    public ForgeVerticalSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        final var usePlayerDestroy = VerticalSlabBlockHooks.removeBlock(state, level, pos, player, willHarvest);
        return usePlayerDestroy || super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
